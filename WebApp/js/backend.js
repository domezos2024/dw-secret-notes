/**
 * Talks to the real dw Secret Notes backend. Verified end-to-end against the
 * live server (2026-08-08, via a real-network probe): the "API" endpoints
 * (android_be_encrypt.php, view_api.php) do NOT return JSON or a simple
 * result string — they return an HTML page containing a <script type="module">
 * that runs the ACTUAL encryption/decryption client-side with the Web Crypto
 * API, then calls back into the Android WebView bridge with the result. The
 * real crypto/storage contract, extracted from that script and confirmed
 * with a live save→get→decrypt→unlink round trip:
 *
 *   - Encrypt: generate a random 16-byte "pass" (its bytes' decimal digits
 *     concatenated with no separator — matches the site's own
 *     `crypto.getRandomValues(new Uint8Array(16)).join('')`), derive an
 *     AES-GCM-256 key via PBKDF2(pass, salt="salt", iterations=100000,
 *     SHA-256), encrypt the UTF-8 text (and, if present, the raw image
 *     bytes) each with their own random 12-byte IV, then POST
 *     `{iv, data, imgIv?, imgData?}` as JSON to
 *     `msg_store.php?action=save&ts=<timestamp>`. The shareable link is
 *     `https://domezos-ware.com/msges/view.php?com=<timestamp>&pass=<pass>`.
 *   - Decrypt: GET `msg_store.php?action=get&com=<alias>` → `{status,
 *     pass_override, payload}`; a not-found alias still returns a
 *     plausible-looking `payload` (anti-enumeration decoy) but `status`
 *     is `"not_found"`. Decrypt `payload` with AES-GCM using
 *     `pass_override ?? suppliedPass`; a real GCM auth-tag mismatch (wrong
 *     pass) throws. After a successful decrypt the site fires
 *     `msg_store.php?action=unlink&com=<alias>` to self-destruct the note.
 *   - Short links (`domezos-ware.com?link=<alias>`, on the Android
 *     side): domezos-ware.com serves a redirect page with the resolved
 *     `const targetUrl = "https://domezos-ware.com/msges/view.php?com=...&pass=...";`
 *     already inlined — regex-extracting it avoids needing to execute that
 *     page's own JS (which also fires its own unlink call on real navigation;
 *     since we only fetch() it here, that side effect doesn't happen, and
 *     the note itself still gets unlinked by the decrypt flow above).
 *
 * This app never creates short links (in the web
 * version, see README) — encryptNote() always returns the long `com=`/`pass=`
 * form — but decryptNote() still resolves short links a user might paste in
 * from someone else's generated link.
 */

import { APP_BASE_URL } from "./config.js";

const STORE_ENDPOINT = `${APP_BASE_URL}/api/msg_store.php`;

// Determine the result host: use a link relative to the current page while
// testing locally, the live server otherwise.
function getResultLongHost() {
  const host = window.location.hostname;
  const protocol = window.location.protocol;
  const pathname = window.location.pathname;
  const currentExt = pathname.endsWith(".php") || pathname.includes(".php/") ? "php" : "html";

  // Use a page-relative link for localhost, 127.0.0.1, or file:// protocol so
  // local testing never has to hit the real domezos-ware.com server.
  if (host === "localhost" || host === "127.0.0.1" || protocol === "file:") {
    const pageDir = pathname.substring(0, pathname.lastIndexOf("/"));
    return `${protocol}//${host}${pageDir}/msges/view.${currentExt}`;
  }

  // Live server for production - always .php
  return `${APP_BASE_URL}/msges/view.php`;
}

const RESULT_LONG_HOST = getResultLongHost();
const SHORT_LINK_HOST = APP_BASE_URL;
export const DEFAULT_PASSPHRASE = "dw_secret_notes_passphrase_2026";

const PBKDF2_SALT = "salt";
const PBKDF2_ITERATIONS = 100000;

export class BackendUnreachableError extends Error {
  constructor(url, cause) {
    super(`Could not reach ${url}`);
    this.url = url;
    this.cause = cause;
  }
}

function pad2(n) {
  return n.toString().padStart(2, "0");
}

/** Matches the site's own timestamp format, used as the message id ("com"). */
function generateTimestamp() {
  const d = new Date();
  const ms = d.getMilliseconds().toString().padStart(3, "0");
  return `${pad2(d.getDate())}.${pad2(d.getMonth() + 1)}.${d.getFullYear()}_${pad2(d.getHours())}-${pad2(d.getMinutes())}-${pad2(d.getSeconds())}-${ms}`;
}

/** Matches the site's own pass generator: 16 random bytes, decimal digits concatenated. */
function generatePass() {
  const bytes = crypto.getRandomValues(new Uint8Array(16));
  return Array.from(bytes).join("");
}

async function deriveKey(pass, usage) {
  const enc = new TextEncoder();
  const keyMaterial = await crypto.subtle.importKey("raw", enc.encode(pass), "PBKDF2", false, ["deriveKey"]);
  return crypto.subtle.deriveKey(
    { name: "PBKDF2", salt: enc.encode(PBKDF2_SALT), iterations: PBKDF2_ITERATIONS, hash: "SHA-256" },
    keyMaterial,
    { name: "AES-GCM", length: 256 },
    false,
    [usage],
  );
}

function base64ToBytes(b64) {
  const binary = atob(b64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
  return bytes;
}

function bytesToBase64(bytes) {
  let binary = "";
  const chunkSize = 0x8000;
  for (let i = 0; i < bytes.length; i += chunkSize) {
    binary += String.fromCharCode.apply(null, bytes.subarray(i, i + chunkSize));
  }
  return btoa(binary);
}

async function safeFetch(url, options) {
  try {
    return await fetch(url, options);
  } catch (err) {
    throw new BackendUnreachableError(url, err);
  }
}

/**
 * @returns {Promise<{ok: true, link: string} | {ok: false, reason: "network"}>}
 */
export async function encryptNote(text, imageBase64) {
  const pass = generatePass();
  const key = await deriveKey(pass, "encrypt");
  const enc = new TextEncoder();

  const iv = crypto.getRandomValues(new Uint8Array(12));
  const encrypted = await crypto.subtle.encrypt({ name: "AES-GCM", iv }, key, enc.encode(text));
  const payload = { iv: Array.from(iv), data: Array.from(new Uint8Array(encrypted)) };

  if (imageBase64) {
    const imageBytes = base64ToBytes(imageBase64);
    const imgIv = crypto.getRandomValues(new Uint8Array(12));
    const imgEncrypted = await crypto.subtle.encrypt({ name: "AES-GCM", iv: imgIv }, key, imageBytes);
    payload.imgIv = Array.from(imgIv);
    payload.imgData = Array.from(new Uint8Array(imgEncrypted));
  }

  const ts = generateTimestamp();
  const res = await safeFetch(`${STORE_ENDPOINT}?action=save&ts=${encodeURIComponent(ts)}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    return { ok: false, reason: "network" };
  }

  const link = `${RESULT_LONG_HOST}?com=${encodeURIComponent(ts)}&pass=${encodeURIComponent(pass)}`;
  return { ok: true, link };
}

/** Resolves a domezos-ware.com short link to its {com, pass} target, or null if unresolvable. */
async function resolveShortLink(alias) {
  const res = await safeFetch(`${SHORT_LINK_HOST}/?link=${encodeURIComponent(alias)}`);
  const text = await res.text();
  const match = text.match(/const targetUrl\s*=\s*"([^"]+)"/);
  if (!match) return null;

  try {
    const url = new URL(match[1]);
    const com = url.searchParams.get("com");
    if (!com) return null;
    return { com, pass: url.searchParams.get("pass") || "" };
  } catch {
    return null;
  }
}

/**
 * @returns {Promise<{ok: true, text: string, image: string|null} | {ok: false, reason: "not_found"|"other"}>}
 */
export async function decryptNote(alias, pass, isShortLink) {
  let com = alias;
  let finalPass = pass || DEFAULT_PASSPHRASE;

  if (isShortLink) {
    const resolved = await resolveShortLink(alias);
    if (!resolved) return { ok: false, reason: "not_found" };
    com = resolved.com;
    finalPass = resolved.pass || finalPass;
  }

  const res = await safeFetch(`${STORE_ENDPOINT}?action=get&com=${encodeURIComponent(com)}`);
  let json;
  try {
    json = await res.json();
  } catch {
    return { ok: false, reason: "other" };
  }

  if (json.status !== "ok" || !json.payload) {
    return { ok: false, reason: "not_found" };
  }

  const effectivePass = json.pass_override ?? finalPass;

  try {
    const key = await deriveKey(effectivePass, "decrypt");
    const dec = new TextDecoder();
    const decrypted = await crypto.subtle.decrypt(
      { name: "AES-GCM", iv: new Uint8Array(json.payload.iv) },
      key,
      new Uint8Array(json.payload.data),
    );
    const text = dec.decode(decrypted);

    let image = null;
    if (json.payload.imgIv && json.payload.imgData) {
      try {
        const imgDecrypted = await crypto.subtle.decrypt(
          { name: "AES-GCM", iv: new Uint8Array(json.payload.imgIv) },
          key,
          new Uint8Array(json.payload.imgData),
        );
        image = bytesToBase64(new Uint8Array(imgDecrypted));
      } catch {
        // Image decrypt failing shouldn't block showing the decrypted text.
      }
    }

    // Fire-and-forget: self-destruct the note, matching the real page's own behavior.
    safeFetch(`${STORE_ENDPOINT}?action=unlink&com=${encodeURIComponent(com)}`).catch(() => {});

    return { ok: true, text, image };
  } catch {
    return { ok: false, reason: "other" };
  }
}
