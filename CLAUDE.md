# CLAUDE.md

Guidance for Claude Code when working in this repository.

## What this app is

**dw Secret Notes** (package `com.snote.domezos`) is a native Android app (Kotlin + Jetpack
Compose) for sending self-destructing encrypted "secret notes" (text and/or a single image) via a
one-time link. There is no local database and no local encryption — the app is a thin client:

1. **Encrypt**: `MainScreen` posts the note (and optional base64 image) to a hidden 1dp `WebView`
   (`SecretWebView`), which `POST`s to `https://domezos-ware.org/api/android_be_encrypt.php`. The
   backend PHP does the actual encryption/storage and returns a one-time link
   (`domezos-ware.org/msges/view.php?...` or the short `snote.fun?link=...` form).
2. **Decrypt**: the reverse — `SecretWebView.decrypt()` loads `view_api.php?com=...&pass=...` (or
   `snote.fun?link=...`) and the JS running in that page calls back into Kotlin via the
   `Android` JavaScript interface (`AndroidInterface.sendAnswer` / `sendImage`) with the
   decrypted content. Notes decrypted in-app auto-clear from the UI after 60 seconds
   (`MainScreen`'s countdown), matching the backend's self-destruct behavior.
3. All server calls are backend-driven business logic; **the actual crypto happens server-side**,
   not in this repo.

The app also handles deep links (`https://domezos-ware.org/msges/view.php` and
`https://snote.fun/...`) and a plain-text `ACTION_SEND` share target, so a note link shared from
elsewhere opens straight into the decrypt flow (see `MainActivity.extractAliasFromIntent`).

## Module / package layout

Single Gradle module: `:app`. Everything lives under `app/src/main/java/com/snote/domezos/`:

- `MainActivity.kt` — entry point, deep-link/share-intent parsing, locale + theme bootstrap,
  premium state, wires `BillingHelper`.
- `DwApplication.kt` — `Application` subclass.
- `navigation/` — `Screen.kt` (route constants), `AppNavigation.kt` (single `NavHost`; back stack
  is intentionally kept flat at `[Main, currentScreen]` so the top-left back button always lands
  on Main — see the comment in `AppNavigation.kt` before changing nav behavior).
- `ui/screens/` — one Composable screen per file: `MainScreen` (encrypt/decrypt), `HelpScreen`,
  `InfoScreen`, `LanguageScreen`, `PremiumScreen`, `TinyUrlScreen`, `TipScreen`.
- `ui/components/` — `SecretWebView` (backend bridge, see above), `AppTopBar`.
- `ui/theme/` — multiple selectable themes (`AppThemes.kt` defines `ALL_THEMES`, default is
  `ClassicTheme`); theme choice is persisted via `Prefs` and also drives widget colors.
- `data/` — `Prefs.kt` (all `SharedPreferences` access, single object, key constants inline),
  `BackendClient.kt` (device handshake + premium activation/deactivation calls),
  `LocaleManager.kt` (per-app language override), `ImageUtils.kt` (bitmap scaling / base64).
- `billing/BillingHelper.kt` — Google Play Billing (see below).
- `widget/` — two home-screen widgets: `SecretWidgetProvider` (quick capture) and
  `LauncherWidgetProvider`, plus `WidgetEncryptActivity` (transparent capture activity) and
  `WidgetRefresher` (pushes theme/state updates to both widget providers).

Resources: `app/src/main/res/values*/strings.xml` — **15 languages** (ar, bn, de, en/default, es,
fr, hi, id, it, ja, ko, pt, ru, ur, zh-rCN). When adding or changing user-facing strings, update
`values/strings.xml` (source of truth) — translations lag behind by nature but keep keys in sync
across all `values-*` files so no locale silently falls back to a missing-key crash risk.

## Monetization

- **Premium** (`premium_tinyurl_30days` one-time INAPP, consumable + `abo_tiny_url_days` SUBS):
  unlocks the alias display and ad-free-ish state. `BillingHelper` talks to Play Billing;
  purchases are **consumed** (not just acknowledged) so the one-time product can be repurchased
  after 30 days expire — see the comment in `consumeStaleInAppPurchases()` before changing that.
  On purchase, premium is activated locally (`Prefs.setPremium`) *and* synced server-side via
  `BackendClient.activatePremium`; subscriptions are re-verified against Play on every app start
  (`MainActivity.refreshPremiumFromServer` → `BillingHelper.syncSubscriptions`).
- **Tip jar** (`tipp_jar`, consumable INAPP, quantity selectable in the Play Console checkout
  sheet): see `TipScreen` / `launchTipBillingFlow`.
- Rotating ad/premium banner strings live in `R.string.ad_1..10` / `premium_active_1..10`.

## Build & run

Standard Gradle Android project, no product flavors currently defined (single `:app` variant).

```
./gradlew assembleDebug          # debug build
./gradlew assembleRelease        # release build (minified, shrunk resources)
./gradlew lint                   # lint (abortOnError = false, so CI won't fail on lint alone)
```

Key config: `compileSdk 37`, `minSdk 26`, `targetSdk 37`, Kotlin `2.2.10`, AGP `9.3.1`, Java 21
toolchain, Compose (no XML layouts except the two widget layouts under `res/layout/`).

There is no automated test suite in this repo yet (no files under `app/src/androidTest` or
`app/src/test`) — verify changes by running the app (`android-cli` skill / Android Studio) rather
than relying on `./gradlew test`.

## Known hardcoded values (intentional, not secrets to "fix" reflexively)

- `PASSPHRASE` in `MainScreen.kt` is the default decrypt passphrase used when a link carries no
  explicit `pass=` param — this mirrors backend default behavior, don't remove it.
- `BackendClient.kt` has `PREMIUM_API_KEY` and `HMAC_SECRET` hardcoded client-side. This repo is
  **private** specifically because of this. If the repo visibility ever changes, or before any
  wider distribution, these need to move server-side / be rotated — flag it rather than silently
  leaving it if you're touching that file.

## Conventions worth following

- Screens follow the same signature pattern: `onNavigate`, `onBack` (except Main), `onThemeChanged`,
  `currentThemeId` — keep new screens consistent with this so `AppNavigation.kt` wiring stays
  uniform.
- All persisted state goes through `Prefs` (single object, `SharedPreferences` under
  `dw_prefs`) — don't create additional `SharedPreferences` files.
- German inline comments/strings appear in a few places (e.g. `AndroidManifest.xml` intent-filter
  comments) — this is a German-market app (domezos-ware.org), bilingual comments are normal here.
- Don't add a backend/crypto abstraction layer in-app — by design, all crypto and storage is
  server-side; the app's job is UI + thin HTTP/WebView bridge.
