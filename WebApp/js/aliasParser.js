/**
 * Port of MainScreen.resolveLink() / parseAliasFromUri() from the Android app.
 * Faithfully replicates the branch order so pasted links/aliases resolve the
 * same way here as they do in the app. The Android redirect-following
 * fallback (HttpURLConnection, up to 5 hops) is intentionally NOT ported —
 * fetch()'s manual-redirect mode can't read Location headers cross-origin,
 * so an unresolvable link returns null here instead (documented parity gap).
 */

const BARE_ALIAS_RE = /^[A-Za-z0-9]{5}$/;

/**
 * @param {string} rawInput
 * @returns {{alias: string, pass: string, isShortLink: boolean} | null}
 */
export function parseAliasFromInput(rawInput) {
  let input = (rawInput || "").trim();
  if (!input) return null;

  // 1. Bare 5-char alphanumeric alias with no "://" and no "." -> short link.
  if (input.length === 5 && !input.includes("://") && !input.includes(".") && BARE_ALIAS_RE.test(input)) {
    return { alias: input, pass: "", isShortLink: true };
  }

  // 2. Prepend https:// if it looks like one of our hosts but has no scheme.
  if (!/^https?:\/\//i.test(input) && (input.includes("snote.fun") || input.includes("domezos-ware.org"))) {
    input = "https://" + input;
  }

  // 3. Still no scheme -> treat as a raw alias if a plausible length, else invalid.
  if (!/^https?:\/\//i.test(input)) {
    if (input.length >= 5 && input.length <= 100) {
      return { alias: input, pass: "", isShortLink: false };
    }
    return null;
  }

  // 4. Full URL: parse and extract com=/link=/pass= per the app's precedence rules.
  let url;
  try {
    url = new URL(input);
  } catch {
    return null;
  }

  const pass = url.searchParams.get("pass") || "";
  const isSnote = url.hostname.includes("snote.fun");
  const comParam = url.searchParams.get("com");
  const linkParam = url.searchParams.get("link");

  if (comParam) {
    return { alias: comParam, pass, isShortLink: false };
  }
  if (linkParam) {
    return { alias: linkParam, pass, isShortLink: true };
  }
  if (isSnote) {
    const path = url.pathname.replace(/^\/+|\/+$/g, "");
    if (path.length >= 5) {
      return { alias: path, pass, isShortLink: true };
    }
  }

  // 5. No alias found via query params/path. The Android app would now follow
  // up to 5 HTTP redirects and re-parse the final URL; that isn't reliably
  // portable to a browser (see module docstring), so we give up here.
  return null;
}
