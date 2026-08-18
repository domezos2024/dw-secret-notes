# dw Secret Notes — Web App

## Overview
=======
Eine reine PHP/HTML/CSS/JS-Nachbildung der Android-App **dw Secret Notes**. Kein Build-Schritt, kein
Framework, kein Server-Code — dieser Ordner ist direkt auf jeden Webserver deploybar.

This repository contains the **Web App** client for **dw Secret Notes** (`com.snote.domezos`), a self-destructing encrypted note service. The companion native Android client (Kotlin + Jetpack Compose) lives in the same repository's `app/` module.

The Web App is a pure HTML/CSS/JS client with **no build step, no framework, and no server-side logic**. Deploy the `WebApp/` folder to any standard PHP-enabled web server and it is production-ready.

---

## System Architecture

```
┌─────────────────────────┐     HTTPS      ┌──────────────────────────────┐
│  Android App            │◄──────────────►│  Backend (domezos-ware.com)  │
│  - Kotlin + Compose     │                │  - PHP (no DB/Redis)         │
│  - 1dp hidden WebView    │                │  - File-based message store  │
│  - Web Crypto API       │                │  - File-based rate limiter   │
├─────────────────────────┤                ├──────────────────────────────┤
│  Web App (this folder)  │◄──────────────►│                              │
│  - Vanilla JS (ES modules)│               │                              │
│  - Web Crypto API       │                │                              │
│  - Service Worker (PWA) │                │                              │
└─────────────────────────┘                └──────────────────────────────┘
```

**Key invariant:** The server is a **dumb ciphertext store**. It never sees plaintext or derived keys. All encryption/decryption happens client-side via the Web Crypto API, either inside the Android WebView or directly in the browser.

---

## Cryptography Protocol

Both clients implement the identical protocol, extracted from the backend's shipped `<script type="module">` and verified end-to-end against the live production server.

### Key Derivation

1. **Passphrase generation:** 16 cryptographically random bytes, concatenated as decimal strings with no separator (e.g., `crypto.getRandomValues(new Uint8Array(16)).join("")`). Resulting string length varies between 16 and 32 characters.
2. **PBKDF2:** `PBKDF2(passphrase, salt="salt", iterations=100_000, hash="SHA-256")` → 256-bit AES key.
3. **Encryption:** AES-256-GCM with a random 12-byte IV per payload.

### Message Structure

- **Text payload:** UTF-8 encoded plaintext → AES-256-GCM ciphertext.
- **Image attachment (optional):** Raw JPEG bytes (scaled to max edge 1600px, quality 0.8) → AES-256-GCM ciphertext with its own independent 12-byte IV.
- **Storage format (JSON):**
  
  ```json
  {
    "iv": [0..11],
    "data": [0..N],
    "imgIv": [0..11],   // optional
    "imgData": [0..M]   // optional
  }
  ```

### Message Alias & Link Format

- **Alias:** A timestamp string `DD.MM.YYYY_HH-MM-SS-fff` (3-digit milliseconds), generated client-side at encrypt time. Used as the storage filename (`<alias>.txt`).
- **Standard link:** `https://domezos-ware.com/msges/view.php?com=<alias>&pass=<passphrase>`
- **Default passphrase (fallback):** `dw_secret_notes_passphrase_2026` — used when a link contains no `pass=` parameter. Mirrors the backend's own default.
- **Short links:** `https://domezos-ware.com?link=<5-char-alias>` — the Web App can resolve these but does not generate them (generation was a former Premium feature, removed after Premium was discontinued).

### Self-Destruct

After a successful decrypt, the client fires a fire-and-forget `GET .../msg_store.php?action=unlink&com=<alias>`, permanently deleting the message from the server. The 60-second UI countdown is a client-side convenience; the server-side deletion happens immediately upon first read.

### Anti-Enumeration

When an alias is not found or already deleted, `msg_get()` returns a **honeypot payload** (`16.11.2025_18-03-49-200.txt`) with a fixed `pass_override`. The client can decrypt it (producing plausible-looking dummy content), but the response `status` is `"not_found"`. This prevents an attacker from probing for valid aliases via timing or error messages.

---

## Backend API Reference

All endpoints require an API key, passed either as:

- Request header: `X-API-Key: <key>`
- Request parameter: `apikey=<key>` (required for Android WebView, which cannot set custom headers via `postUrl()`/`loadUrl()`)

### Endpoints

| Endpoint | Method | Purpose | Response |
|----------|--------|---------|----------|
| `/api/msg_store.php?action=save&ts=<alias>` | POST | Store encrypted payload | `{status:"ok"}` |
| `/api/msg_store.php?action=get&com=<alias>` | GET | Retrieve encrypted payload | `{status, pass_override, payload}` |
| `/api/msg_store.php?action=unlink&com=<alias>` | GET | Delete message file | `{status:"ok"}` |
| `/api/android_be_encrypt.php` | POST | Android encrypt bridge | HTML page with embedded JS module |
| `/api/view_api.php?com=<alias>&pass=<pass>` | GET | Android decrypt bridge | HTML page with embedded JS module |

### Rate Limiting

File-based, atomic rate limiting via `flock()`. No database or Redis required — designed for standard shared hosting.

| Tier | Per Minute | Per Day | Notes |
|------|-----------|---------|-------|
| `internal` | 120 | 50,000 | App + Web App first-party traffic |
| `trusted` | 30 | 5,000 | Paid tier |
| `free` | 10 | 1,000 | Self-service via GitHub issue/PR |

Exceeding limits returns `HTTP 429` with `Retry-After: 60`.

### API Key Management

- **Public keys (safe to ship in source code):** `APP_API_KEY` in `js/config.js` (Web App) and `Backend.API_KEY` in `app/src/main/java/com/snote/domezos/data/Backend.kt` (Android). These are **quota tags, not secrets**.
- **Private keys:** `api/data/keys.php` (gitignored). Template: `api/data/keys.example.php`.
- **Key format:** `return ['hex_key' => ['owner' => '...', 'tier' => '...', 'revoked' => false]];`

---

## Web App Structure

### File Layout

---
WebApp/
├── index.php / index.html        # Main app shell (Encrypt / Decrypt tabs)
├── help.php / help.html          # Help & FAQ
├── info.php / info.html          # About
├── impressum.php                 # Legal notice (§5 TMG / §55 RStV)
├── msges/
│   └── view.php                  # Decrypt-from-link page (auto-decrypts on load)
├── api/
│   ├── config.php                # APP_HOST, APP_BASE_URL, MESSAGES_DIR
│   ├── auth.php                  # API key enforcement + file-based rate limiting
│   ├── msg_store.php             # CRUD for encrypted message files
│   ├── android_be_encrypt.php    # Encrypt bridge for Android WebView
│   ├── view_api.php              # Decrypt bridge for Android WebView
│   └── data/
│       ├── keys.example.php      # Template for API keys
│       └── keys.php              # Real keys (gitignored)
├── .well-known/
│   └── assetlinks.json           # Android App Links verification
├── .htaccess                     # Enforces application/json for assetlinks.json
├── manifest.webapp.json          # PWA manifest (standalone identity)
├── js/
│   ├── config.js                 # APP_HOST, APP_BASE_URL, APP_API_KEY
│   ├── backend.js                # Core crypto (encryptNote, decryptNote, deriveKey)
│   ├── aliasParser.js            # Link/alias parsing (ported from Android MainScreen)
│   ├── imageUtils.js             # Image downscale + base64 (ported from ImageUtils.kt)
│   ├── i18n.js                   # 15-language system with RTL support
│   ├── theme.js                  # 16-theme system
│   ├── main.js                   # Main page UI logic
│   ├── help.js                   # Help page logic
│   ├── info.js                   # Info page logic
│   ├── impressum.js              # Impressum page logic
│   └── view.js                   # msges/view.php decrypt-from-link logic
├── css/
│   ├── themes.css                # 16 [data-theme="id"] custom property blocks
│   └── base.css                  # Layout, typography, components
├── i18n/
│   ├── en.json                   # Source strings (15 other locales mirror keys)
│   ├── de.json
│   ├── es.json
│   ├── zh-CN.json
│   ├── hi.json
│   ├── ar.json
│   ├── pt.json
│   ├── bn.json
│   ├── ru.json
│   ├── ja.json
│   ├── fr.json
│   ├── ur.json
│   ├── id.json
│   ├── ko.json
│   └── it.json
└── assets/
    ├── app_icon_192.png
    └── app_icon_512.png
---

### Deployment

1. Upload the entire `WebApp/` contents to your web server (domain root or subdirectory — all paths are relative).
2. Ensure PHP 8+ with `json` extension and `flock()` support (standard on shared hosting).
3. Copy `api/data/keys.example.php` to `api/data/keys.php` and populate with at least one `free`-tier key.
4. Done. No `package.json`, no build step, no Node.js required.

### Dual-Stack: `index.php` + `index.html`

The Web App ships as **both `.php` and `.html`** for every page. `index.php` is the production entry point on `domezos-ware.com`. The `.html` variants exist for local testing and for environments where the `.php` extension is mapped to a different handler. `js/main.js` normalizes internal links at runtime to match the current page's extension.

---

## PWA ("Add to Home Screen")

The Web App has its **own, independent** PWA identity, parallel to the existing `index.php` app on the domain:

- `manifest.webapp.json` declares `"id": "/index.php"` and `"start_url": "/index.php"`, giving it a separate installation identity from any other manifest on the same origin.
- All pages register the shared `/sw.js` service worker (a passthrough without app-specific caching logic — safe to share).
- Icons are reused from `/assets/app_icon_192.png` and `_512.png`. `theme_color` and `background_color` are set to `#050D1F` (Classic Theme background) to distinguish the splash screen from the `index.php` app.

To disable PWA installability for the Web App, simply delete `manifest.webapp.json` and remove the `<link rel="manifest">` tags — the `index.php` app is unaffected.

---

## Android App (Brief)

The Android client lives under `app/src/main/java/com/snote/domezos/`.

### Key Components

| File | Responsibility |
| ------ | --------------- |
| `MainActivity.kt` | Entry point, deep-link/share-intent parsing, locale + theme bootstrap |
| `MainScreen.kt` | Encrypt/Decrypt UI, image picker, 60s countdown timer, share intent |
| `SecretWebView.kt` | 1dp hidden WebView that loads `android_be_encrypt.php` / `view_api.php` and bridges results via `AndroidInterface` (`@JavascriptInterface`) |
| `Backend.kt` | `HOST`, `BASE_URL`, `API_KEY` (quota tag, not a secret) |
| `Prefs.kt` | Single `SharedPreferences` instance (`dw_prefs`) |
| `ImageUtils.kt` | Image decoding, scaling, base64 preparation for upload |
| `LocaleManager.kt` | Runtime locale override |

### Deep Links & App Links

- **Deep links:** `https://domezos-ware.com/msges/view.php?com=<alias>&pass=<pass>` and `https://domezos-ware.com?link=<alias>`
- **Android App Links:** Verified via `.well-known/assetlinks.json` for package `com.snote.domezos`
- **Share targets:** `ACTION_SEND` intents with `text/plain` route directly into the decrypt flow

---

## Internationalization

15 locales are supported, mirroring the Android app exactly:

`en`, `de`, `es`, `zh-CN`, `hi`, `ar`, `pt`, `bn`, `ru`, `ja`, `fr`, `ur`, `id`, `ko`, `it`

- Strings live in `i18n/<tag>.json`. Add a new locale by copying `en.json`, translating values, and registering the tag in `LANGUAGES` inside `js/i18n.js`.
- Missing locales fall back to English at runtime (with a console warning).
- **RTL:** Arabic (`ar`) and Urdu (`ur`) are listed in `RTL_LANGS`; `applyDirection()` sets `<html dir="rtl">` automatically.
- Plural handling is simplified to `_one` / `_other` suffixes per language (no full ICU plural rules).

---

## Theming

16 themes are available, ported from the Android app's `AppThemes.kt`:

`classic`, `dark`, `light`, `midnight`, `forest`, `ocean`, `cyberpunk`, `dracula`, `sunset`, `nordic`, `matrix`, `sakura`, `golden`, `ruby`, `electric`, `ghost`, `solarized`

- Color values are defined as CSS custom properties in `css/themes.css` under `[data-theme="<id>"]`.
- Theme metadata (id + swatch color for the picker) is registered in `THEMES` inside `js/theme.js`.
- Display names are stored as `theme_<id>` keys in every `i18n/*.json` file.
- Selection is persisted to `localStorage` (`dw_theme`).

---

## Known Parity Gaps to the Android App

1. **Redirect following on link paste:** The Android app (`MainScreen.resolveLink`) follows up to 5 HTTP redirects via `HttpURLConnection` and re-parses the final URL. The Web App's `aliasParser.js` intentionally does **not** port this behavior — `fetch()` with `redirect: "manual"` cannot read cross-origin `Location` headers in a browser context, so unresolvable links return `null` instead.
2. **ICU plural forms:** Some languages (e.g., Arabic, Russian) have more than two plural forms (`few`, `many`, `zero`) in the Android app. The Web App uses a simplified `_one` / `_other` scheme.
3. **Premium / Tip features:** These are Android-only (in-app review, Play Store integration). The Web App does not expose them.

---

## Verification Status

### Verified

- **End-to-end crypto round-trip:** Full encrypt → store → retrieve → decrypt → unlink cycle tested against `domezos-ware.com` on 2026-08-08. Plaintext before/after matched exactly.
- **Backend protocol:** All endpoints (`msg_store.php`, `android_be_encrypt.php`, `view_api.php`) probed with real HTTP requests. Honeypot anti-enumeration behavior confirmed.
- **Alias/link parsing:** `parseAliasFromInput` tested against bare 5-char aliases, `?link=`, `?com=&pass=`, path-style aliases, and invalid input.
- **Image pipeline:** `resizeImageToBase64` verified (2000×1000 → 1600×800, no `data:` prefix in output).
- **UI rendering:** Theme switching, language switching (including RTL for Arabic), tab navigation, and deep-link auto-decrypt tested in headless Chromium. Console clean.

### Open

- A full encrypt/decrypt round-trip driven through the Web App's own UI (clicking "Encrypt" / "Decrypt" in a real browser) has not yet been executed manually. The underlying protocol is verified in isolation; the UI glue should be tested after deployment.

---

## Security Notes

- **No server-side encryption.** The server never derives keys, never sees plaintext, and never handles unencrypted images.
- **API keys are public quota tags.** They ship in both the Android APK and the Web App's `js/config.js`. Their purpose is rate-limit tier assignment, not access control to sensitive data.
- **CORS:** `Access-Control-Allow-Origin: *` is set on all API endpoints, allowing `fetch()` from any hosting origin.
- **FLAG_SECURE:** The Android app sets `WindowManager.LayoutParams.FLAG_SECURE` in release builds to prevent screenshots and screen recording of the WebView content.
