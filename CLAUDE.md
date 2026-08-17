# CLAUDE.md

Guidance for Claude Code when working in this repository.

## What this app is

**dw Secret Notes** (`com.snote.domezos`) — native Android app (Kotlin + Jetpack Compose) for sending self-destructing encrypted notes (text + optional image) via a one-time link. Thin client, no local DB, no local crypto.

**Encrypt flow:** `MainScreen` posts note + optional base64 image to a hidden 1dp `SecretWebView`, which POSTs to `https://domezos-ware.com/api/android_be_encrypt.php`. Backend returns an HTML page with an embedded `<script type="module">` that runs client-side in the WebView's JS engine: generates a random passphrase, derives AES-256-GCM key via PBKDF2 (`salt="salt"`, 100k iterations, SHA-256), encrypts, then calls `api/msg_store.php?action=save` to store ciphertext + IV. Returns a one-time link.

**Decrypt flow:** `SecretWebView.decrypt()` loads `view_api.php?com=...&pass=...`. The embedded JS fetches ciphertext, decrypts client-side, calls back into Kotlin via `AndroidInterface.sendAnswer` / `sendImage`. UI auto-clears after 60s countdown.

**Key correction (verified 2026-08-08):** Crypto is NOT server-side. The server ships the crypto logic as HTML/JS on each call — it executes in the WebView. Server never sees plaintext or derived keys. `WebApp/js/backend.js` reimplements this exact protocol.

Deep links (`https://domezos-ware.com/msges/view.php`, `https://domezos-ware.com?link=...`) and `ACTION_SEND` share targets route straight into the decrypt flow via `MainActivity.extractAliasFromIntent`.

## Module / package layout

Single Gradle module `:app`. Everything under `app/src/main/java/com/snote/domezos/`:

- `MainActivity.kt` — entry point, deep-link/share-intent parsing, locale + theme bootstrap.
- `DwApplication.kt` — `Application` subclass.
- `navigation/` — `Screen.kt` (route constants), `AppNavigation.kt` (flat back stack `[Main, currentScreen]` — keep it flat, don't change nav without reading the comment in `AppNavigation.kt`).
- `ui/screens/` — `MainScreen`, `HelpScreen`, `InfoScreen`, `LanguageScreen`, `TinyUrlScreen`.
- `ui/components/` — `SecretWebView` (backend bridge), `AppTopBar`.
- `ui/theme/` — `AppThemes.kt` defines `ALL_THEMES` (default `ClassicTheme`); theme choice persisted via `Prefs`, drives widget colors.
- `data/` — `Prefs.kt` (all SharedPreferences, single object, `dw_prefs`), `Backend.kt` (host + API key), `LocaleManager.kt`, `ImageUtils.kt`.
- `widget/` — `SecretWidgetProvider` (quick capture), `LauncherWidgetProvider`, `WidgetEncryptActivity`, `WidgetRefresher`.

Strings: `app/src/main/res/values*/strings.xml` — **15 languages**. Add/change strings in `values/strings.xml` first; keep keys in sync across all `values-*` files.

## Monetization

None. Billing was fully removed — app is free, no IAP. Don't reintroduce billing infra. Ad-banner strings (`R.string.ad_1..10`) now describe security features, not a paid tier.

## Backend API access

Every request needs `X-API-Key` header or `apikey` param. App uses the param form (WebView doesn't support custom headers). Key lives in `Backend.API_KEY` — it's a rate-limit quota tag, not a secret. See `WebApp/api/auth.php` for tiers and `WebApp/api/data/keys.example.php` for third-party key format.

## Build & run

```bash
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew lint                   # abortOnError = false
./gradlew testDebugUnitTest
```

Config: `compileSdk 37`, `minSdk 26`, `targetSdk 37`, Kotlin 2.2.10, AGP 9.3.1, Java 21, Compose (no XML layouts except two widget layouts under `res/layout/`). No instrumentation suite — verify UI/WebView changes by running on device.

## Known hardcoded values (intentional)

- `PASSPHRASE` in `MainScreen.kt` — default decrypt passphrase when link has no `pass=` param. Mirrors backend default. Do not remove.
- `Backend.API_KEY` — public-safe quota tag, not a capability secret. Repo can stay public.

## Conventions

- Screen signature: `onNavigate`, `onBack` (except Main), `onThemeChanged`, `currentThemeId` — keep new screens consistent.
- All persisted state via `Prefs` (single object, `dw_prefs`) — no additional SharedPreferences files.
- Don't add a backend/crypto abstraction layer — crypto is client-side JS shipped from backend per call; storage is server-side. Neither belongs in this Kotlin codebase.
