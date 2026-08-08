/**
 * Talks to the same backend the Android app uses (see SecretWebView.kt).
 * The Android app is a WebView that lets server-rendered pages call back
 * into Kotlin via `Android.sendAnswer(...)` / `Android.sendImage(...)` /
 * `Android.notifyDataReady({url})`. That JS bridge doesn't exist in a plain
 * browser, and the real response shape of these PHP endpoints isn't visible
 * anywhere in this repo (this sandbox also has no network access to verify
 * it live) — so this module treats a plain fetch() response as a "virtual
 * WebView": if it's JSON, parse it directly; if it's HTML, scan it for the
 * same Android.* call patterns the real WebView bridge would have received.
 *
 * KNOWN LIMITATION (see WebApp/README.md): whether domezos-ware.org sends
 * CORS headers permitting fetch() from an arbitrary hosting origin, and
 * which of the branches below actually fires, can only be confirmed once
 * this is deployed to a real server and inspected via browser devtools.
 */

const ENCRYPT_ENDPOINT = "https://domezos-ware.org/api/android_be_encrypt.php";
const DECRYPT_ENDPOINT = "https://domezos-ware.org/api/view_api.php";
const SHORT_LINK_HOST = "https://snote.fun";
export const DEFAULT_PASSPHRASE = "dw_secret_notes_passphrase_2026";

/** "auto" | "json" | "html-bridge-scrape" | "redirect" — force a branch once
 * the real response shape is known; "auto" tries JSON then falls back to
 * scraping the bridge-call patterns out of an HTML response. */
export let RESPONSE_MODE = "auto";
export function setResponseMode(mode) {
  RESPONSE_MODE = mode;
}

export function buildDecryptUrl(alias, pass, isShortLink) {
  if (isShortLink) {
    return `${SHORT_LINK_HOST}?link=${encodeURIComponent(alias)}`;
  }
  return `${DECRYPT_ENDPOINT}?com=${encodeURIComponent(alias)}&pass=${encodeURIComponent(pass)}`;
}

/**
 * Scans a response body for the Android.* JS-bridge calls the legacy page
 * would have made, exactly as if this were the WebView reading them.
 */
export function parseBridgeResponse(text) {
  const answerMatch = text.match(/Android\.sendAnswer\((['"])([\s\S]*?)\1\)/);
  const imageMatch = text.match(/Android\.sendImage\((['"])([\s\S]*?)\1\)/);
  const notifyMatch = text.match(/Android\.notifyDataReady\((\{[\s\S]*?\})\)/);

  let notifyUrl = null;
  if (notifyMatch) {
    try {
      notifyUrl = JSON.parse(notifyMatch[1]).url || null;
    } catch {
      notifyUrl = null;
    }
  }

  return {
    answer: answerMatch ? answerMatch[2] : null,
    image: imageMatch ? imageMatch[2] : null,
    notifyUrl,
  };
}

function isDecryptFailure(answer) {
  return (
    answer.startsWith("ERROR:") ||
    answer === "not_found" ||
    answer.includes("Nachricht nicht gefunden")
  );
}

async function fetchRaw(url, options) {
  try {
    const res = await fetch(url, options);
    const text = await res.text();
    return { text, contentType: res.headers.get("content-type") || "" };
  } catch (err) {
    // Most likely a CORS failure (missing Access-Control-Allow-Origin on the
    // backend) or a genuine network error — either way we can't read the
    // response from here.
    throw new BackendUnreachableError(url, err);
  }
}

export class BackendUnreachableError extends Error {
  constructor(url, cause) {
    super(`Could not reach ${url} (CORS or network error)`);
    this.url = url;
    this.cause = cause;
  }
}

/**
 * @param {string} text, e.g. "en" | "de" | ...
 * @returns {{answer: string|null, image: string|null, notifyUrl: string|null}}
 */
function extractBridgePayload(text, contentType) {
  if (RESPONSE_MODE === "json" || (RESPONSE_MODE === "auto" && contentType.includes("application/json"))) {
    try {
      const json = JSON.parse(text);
      return {
        answer: json.url || json.answer || json.text || (json.ok === false ? json.error || json.message : null),
        image: json.image || null,
        notifyUrl: json.url || null,
      };
    } catch {
      // fall through to bridge scraping
    }
  }
  return parseBridgeResponse(text);
}

/**
 * @returns {Promise<{ok: true, link: string} | {ok: false, message: string}>}
 */
export async function encryptNote(text, imageBase64) {
  const body = new URLSearchParams();
  body.set("write", text);
  if (imageBase64) {
    body.set("image", imageBase64);
  }

  const { text: responseText, contentType } = await fetchRaw(ENCRYPT_ENDPOINT, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: body.toString(),
  });

  const { answer, notifyUrl } = extractBridgePayload(responseText, contentType);
  const result = (answer && answer.startsWith("http")) ? answer : notifyUrl;

  if (result && result.startsWith("http")) {
    return { ok: true, link: result };
  }
  return { ok: false, message: answer || "Encryption failed: unrecognized server response." };
}

/**
 * @returns {Promise<{ok: true, text: string, image: string|null} | {ok: false, reason: "not_found"|"other", raw: string}>}
 */
export async function decryptNote(alias, pass, isShortLink) {
  const url = buildDecryptUrl(alias, pass, isShortLink);
  const { text: responseText, contentType } = await fetchRaw(url, { method: "GET" });
  const { answer, image } = extractBridgePayload(responseText, contentType);

  if (answer == null) {
    return { ok: false, reason: "other", raw: "" };
  }
  if (isDecryptFailure(answer)) {
    return { ok: false, reason: answer === "not_found" || answer.includes("Nachricht nicht gefunden") ? "not_found" : "other", raw: answer };
  }
  return { ok: true, text: answer === " " ? "" : answer, image: image || null };
}
