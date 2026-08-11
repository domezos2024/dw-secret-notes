# Public Release Preparation Plan — dw Secret Notes

## Mission

Prepare the GitHub repo for public release by downloading all server-side files the **dw Secret
Notes Android app + WebApp** depend on, sanitizing every secret, documenting the database
schema and deployment process — **without** modifying the live server or local build. Do **not**
flick the repo public yet; just make it ready.

## 1. File Scope — What to Download vs. Exclude

### 1a. Download & sanitize (dw Secret Notes related)

#### `WebApp/api/` — New directory (download all 9 PHP files)

| File | Has secrets? | Secrets to externalize |
|------|-------------|------------------------|
| `config.php` | YES | `DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASS`, `API_SECRET` |
| `android_be_encrypt.php` | YES | `$secret = "88888888-..."` (PayPal HMAC); also has embedded HTML/JS |
| `encrypt_handshake.php` | YES | `$APP_HMAC_SECRET` (already `getenv()` fallback, keep pattern); requires `assets/db.php` |
| `activate_premium.php` | YES | `API_KEY = 'NMsePsV2026'` |
| `deactivate_premium.php` | YES | `API_KEY_DEACT = 'NMsePsV2026'` |
| `msg_store.php` | YES | `MSG_HONEYPOT_PASS` |
| `premium_status.php` | YES | `PREMIUM_SET_SECRET` |
| `view_api.php` | NO | Pure HTML/JS template (embeds crypto JS); has `domezos-ware.org` URLs — make domain configurable |
| `index.php` | NO (direct) | Uses `API_SECRET` from `config.php` (already externalized) |

#### `WebApp/assets/` — New directory (download 4 PHP files)

| File | Has secrets? | Secrets |
|------|-------------|---------|
| `db.php` | YES | `DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASS` |
| `premium_check.php` | NO | Redirect to `premium_status.php` |
| `set_premium.php` | NO | Redirect to `premium_status.php` |
| `new_device.php` | NO | Redirect to `premium_status.php` |

#### Root-level `WebApp/` (download existing + new files)

| File | Has secrets? | Notes |
|------|-------------|-------|
| `.htaccess` | NO | Security headers, canonical redirects, CORS |
| `clean_up.php` | NO | Cron job — file expiration |
| `msges.php` | NO | Short-link redirector; has `domezos-ware.org` URLs → make configurable |
| `save_link.php` | NO | File-based URL shortener (premium short links) |
| `unlink.php` | NO | Short-link deleter |
| `manifest.json` | NO | PWA manifest; references `/assets/` images |
| `robots.txt` | NO | Standard robots |
| `sitemap.xml` | NO | Sitemap |
| `sw.js` | NO | Passthrough service worker |
| `favicon.ico` | NO | Binary |
| `google3c9a04f0d6f37a5f.html` | NO | Google verification?? Put on Repo or Users get their own?? |
| `privacy.snote.html` | NO | Privacy policy (mentions app) |

#### `WebApp/msges/` — Additional files (download 3 PHP files)

| File | Has secrets? | Notes |
|------|-------------|-------|
| `save.php` | NO | Backward-compat redirect → `msg_store.php?action=save` |
| `unlink.php` | NO | Backward-compat redirect → `msg_store.php?action=unlink` |
| `viewty.php` | YES | Legacy encrypt/decrypt page; has honeypot password hardcoded |

#### `WebApp/.well-known/` — New directory

| File | Has secrets? | Notes |
|------|-------------|-------|
| `assetlinks.json` | NO | Android App Links verification (public SHA256 fingerprints only) should tell users of Repo how to setup their or fingerprints and assetlinks.json|

### 1b. Explicitly EXCLUDE from repo (not dw Secret Notes related)

| Path | Reason |
|------|--------|
| `links/` directory (entire) | snote.fun TinyURL service — separate application |
| `links/firebase-adminsdk.json` | Firebase private key for snote.fun (secret) |
| `links/api.php` | snote.fun TinyURL API (Firebase/PayPal secrets) |
| `links/paypal-payment.php` | snote.fun PayPal subscriptions |
| `links/donate.php` | snote.fun donations |
| `links/save_tiny.php` | snote.fun short URL creation |
| `links/delete_link.php` | snote.fun link deletion |
| `links/shrink_api.php` | snote.fun shrink API |
| `links/index.php` | snote.fun redirector (NOT needed — WebApp resolves short links via regex, snote.fun is separate domain) |
| `links/*.html` | snote.fun UI pages |
| `links/*.log` | Sensitive logs |
| `links/._rate/` | Rate-limit data |
| `links/news/` | Visitor analytics data |
| `links/datenschutz/` | snote.fun privacy page |
| `links/impressum/` | snote.fun impressum page |
| `links/.well-known/` | snote.fun verification |
| `shop/` directory (entire) | Separate webshop |
| `forum/` directory (entire) | Separate forum |
| `app-release/` | Static download page |
| `tool.html` | Unrelated tool page |
| `dothis.txt` | Private admin message |
| `msges/*.txt` | Encrypted user messages |
| `.well-known/snote-domezos-cloud-*.json` | GCP service account key (unused by dw Secret Notes code — exclude or provide as `.example`) |
| `.well-known/apple-developer-merchantid-domain-association` | Apple Pay (PayPal) — not dw Secret Notes |

### 1c. Already in repo (no action needed)

All files currently in `WebApp/` (frontend HTML, CSS, JS, i18n, `msges/view.php`, `msges/view.html`).

## 2. Secret Sanitization Rules

### PHP files — env var pattern

Every hardcoded secret in downloaded PHP files must be replaced with:

```php
// BEFORE:
define('DB_PASS', 'Real.Fuck69');

// AFTER:
define('DB_PASS', getenv('DB_PASS') ?: 'DeinDatenbankPasswortHierEinfuegen');
```

**Naming convention for env vars** (consistent across all files):

| Secret type | Env var name |
|-------------|-------------|
| MySQL host | `DB_HOST` |
| MySQL database | `DB_NAME` |
| MySQL username | `DB_USER` |
| MySQL password | `DB_PASS` |
| Secret Notes API key (admin_panel.php, index.php) | `API_SECRET` |
| Admin panel token | `ADMIN_TOKEN` |
| Android app HMAC secret | `APP_HMAC_SECRET` |
| Android app premium API key | `PREMIUM_API_KEY` |
| PayPal webhook HMAC secret | `PREMIUM_HMAC_SECRET` |
| Msg store honeypot password | `HONEYPOT_PASS` |
| Msg store honeypot filename | `HONEYPOT_FILE` |
| PayPal client ID | `PAYPAL_CLIENT_ID` |
| PayPal client secret | `PAYPAL_CLIENT_SECRET` |
| PayPal plan ID | `PAYPAL_PLAN_ID` |
| PayPal webhook ID | `PAYPAL_WEBHOOK_ID` |

**For `firebase-adminsdk.json`:** Create `firebase-adminsdk.json.example`
with placeholder structure and instructions. The PHP code reads the file path from
`getenv('FIREBASE_SA_FILE')` with fallback to `__DIR__ . '/firebase-adminsdk.json'`.
`firebase-adminsdk.json` itself goes in `.gitignore`.

### Android app — BuildConfig pattern

| File | Current | Replace with |
|------|---------|-------------|
| `BackendClient.kt:20` | `private const val HMAC_SECRET = "change-me"` | `private val HMAC_SECRET = BuildConfig.HMAC_SECRET` |
| `BackendClient.kt:77` | `private const val PREMIUM_API_KEY = "NMsePsV2026"` | `private val PREMIUM_API_KEY = BuildConfig.PREMIUM_API_KEY` |
| `BackendClient.kt:19` | `internal var BASE_URL = "https://domezos-ware.org/api/"` | `internal var BASE_URL = BuildConfig.BACKEND_BASE_URL` |
| `SecretWebView.kt:23-25` | `ENCRYPT_ENDPOINT`, `DECRYPT_ENDPOINT`, `SHORT_LINK_HOST` (hardcoded) | `BuildConfig.ENCRYPT_ENDPOINT`, `BuildConfig.DECRYPT_ENDPOINT`, `BuildConfig.SHORT_LINK_HOST` |

In `app/build.gradle.kts` `defaultConfig`:
```kotlin
buildConfigField("String", "HMAC_SECRET", "\"DeinHmacSecretHierEinfuegen\"")
buildConfigField("String", "PREMIUM_API_KEY", "\"DeinPremiumApiKeyHierEinfuegen\"")
buildConfigField("String", "BACKEND_BASE_URL", "\"https://deine-domain.de/api/\"")
buildConfigField("String", "ENCRYPT_ENDPOINT", "\"https://deine-domain.de/api/android_be_encrypt.php\"")
buildConfigField("String", "DECRYPT_ENDPOINT", "\"https://deine-domain.de/api/view_api.php\"")
buildConfigField("String", "SHORT_LINK_HOST", "\"https://snote.fun\"")
```

Create `gradle.properties.example`:
```properties
# Android app secrets — copy to gradle.properties and fill with real values
# Do NOT commit gradle.properties (it is gitignored)
```

Add to `.gitignore`:
```
# Android secrets
/gradle.properties
```

**Keep local `gradle.properties` with real values** — it is gitignored, so the real secrets
stay local. The repo only has `gradle.properties.example` and `build.gradle.kts` with
placeholder `buildConfigField` values.

### `PASSPHRASE` / `DEFAULT_PASSPHRASE` — KEEP (not a secret)

Both `MainScreen.kt:106` and `WebApp/js/backend.js:64` contain:
`dw_secret_notes_passphrase_2026`

This is a **protocol constant** (the default passphrase for links without `?pass=`).
It is already public in `WebApp/js/backend.js` (the repo's public WebApp JS).
**Do not change it.** Add a comment clarifying it's a protocol default, not a secret.

## 3. Database Schema

The app uses MySQL via PDO (`$pdo` from `assets/db.php`). Three tables are used by the
dw Secret Notes backend. Extract the schema from the code:

### `devices` table

```sql
CREATE TABLE devices (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    token         VARCHAR(255) NOT NULL UNIQUE,  -- device token from app
    premium       TINYINT(1) DEFAULT 0,          -- 1 = premium, 0 = free
    duration      INT DEFAULT 0,                 -- premium duration in days
    created_at    DATETIME,                      -- when premium was activated
    last_used     DATETIME,                      -- last handshake time
    open_count    INT DEFAULT 0                  -- app open counter
);
```

**Referenced in:** `api/encrypt_handshake.php` (ensure_device, evaluate_and_maybe_expire),
`api/premium_status.php` (deviceRegister, premiumCheck), `api/activate_premium.php`,
`api/deactivate_premium.php`, `api/admin_panel.php`, `api/index.php`.

### `app_config` table

```sql
CREATE TABLE app_config (
    config_key   VARCHAR(100) NOT NULL PRIMARY KEY,
    config_value VARCHAR(500) NOT NULL DEFAULT ''
);
```

**Referenced in:** `api/encrypt_handshake.php` (get_app_version), `api/admin_panel.php`.
**Pre-populate:** Insert `apk_version` and `apk_version_code`:
```sql
INSERT INTO app_config (config_key, config_value) VALUES
('apk_version', '4.6'),
('apk_version_code', '43');
```

### `notifications` table

```sql
CREATE TABLE notifications (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    device_token VARCHAR(255),
    type         VARCHAR(100),
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_read      TINYINT(1) DEFAULT 0
);
```

**Referenced in:** `api/premium_status.php` (checkNotification), `api/admin_panel.php` (send_msg).

### Full schema SQL file

Create `WebApp/database/schema.sql` with the above DDL. Also create
`WebApp/database/README.md` with MySQL import instructions.

## 4. Domain Configuration Guide

The codebase has hardcoded references to two domains. These must be changed when deploying
to a different server/domain.

### 4a. Android app (`build.gradle.kts` buildConfigField)

| BuildConfig field | Default in repo | What to change |
|---|---|---|
| `BACKEND_BASE_URL` | `https://deine-domain.de/api/` | Set to your server's API base URL |
| `ENCRYPT_ENDPOINT` | `https://deine-domain.de/api/android_be_encrypt.php` | Full URL to encrypt endpoint |
| `DECRYPT_ENDPOINT` | `https://deine-domain.de/api/view_api.php` | Full URL to decrypt endpoint |
| `SHORT_LINK_HOST` | `https://snote.fun` | Your short-link domain (or leave as snote.fun if using that service) |
| `HMAC_SECRET` | `DeinHmacSecretHierEinfuegen` | Must match server's `APP_HMAC_SECRET` env var |
| `PREMIUM_API_KEY` | `DeinPremiumApiKeyHierEinfuegen` | Must match server's `PREMIUM_API_KEY` env var |

**Note:** `SHORT_LINK_HOST` is only used for Premium short-link resolution. If you don't
need short links, the app still works with long `?com=...&pass=...` URLs.

### 4b. WebApp JS (`WebApp/js/backend.js`)

| Variable | Line | Default | What to change |
|---|---|---|---|
| `STORE_ENDPOINT` | 41 | `https://domezos-ware.org/api/msg_store.php` | Your server's `msg_store.php` URL |
| `RESULT_LONG_HOST` | 59 | `https://domezos-ware.org/msges/view.php` | Your server's `view.php` URL |
| `SHORT_LINK_HOST` | 63 | `https://snote.fun` | Your short-link domain |
| `DEFAULT_PASSPHRASE` | 64 | `dw_secret_notes_passphrase_2026` | **KEEP** — protocol constant |

Current code already has localhost detection (lines 53–58). For a real domain deployment,
the production URLs must be changed. Make these configurable via a config object at the top
of the file.

### 4c. PHP files

PHP files reference `domezos-ware.org` and `snote.fun` in URL generation. Create
`WebApp/config/domains.php` that defines these via env vars:

```php
<?php
// Central domain configuration — override via environment variables
define('APP_DOMAIN',       getenv('APP_DOMAIN') ?: 'domezos-ware.org');
define('SHORT_LINK_DOMAIN', getenv('SHORT_LINK_DOMAIN') ?: 'snote.fun');
define('APP_BASE_URL',     'https://' . APP_DOMAIN);
define('API_BASE_URL',     APP_BASE_URL . '/api/');
define('MESSAGES_BASE_URL', APP_BASE_URL . '/msges/');
```

All PHP files that generate URLs should `require_once __DIR__ . '/../config/domains.php'`
and use these constants instead of hardcoded strings. For embedded JS in PHP files
(`android_be_encrypt.php`, `view_api.php`, `encrypt_handshake.php`), use PHP variables in
the template.

### 4d. `assetlinks.json`

The `assetlinks.json` file must be placed at `https://your-domain/.well-known/assetlinks.json`.
Update the `package_name` (if different) and regenerate SHA256 fingerprints for your
release keystore. Instructions for generating fingerprints are in §6.

## 5. Each Secret: Location + How to Change

### Secret 1: `DB_PASS` / MySQL password
- **Where in repo:** `WebApp/api/config.php` (line 7), `WebApp/assets/db.php` (line 6),
  `WebApp/api/admin_panel.php` (line 16 — `ADMIN_TOKEN` happens to equal DB_PASS)
- **Where on server:** Same files, hardcoded
- **How to change:** Change MySQL password in hosting control panel, then set `DB_PASS`
  env var on server
- **Used by:** All PHP files that call `require_once .../config.php` or `.../db.php`
- **What happens if wrong:** DB connection fails, all device/premium management stops;
  message storage still works (file-based, doesn't use DB)

### Secret 2: `API_SECRET`
- **Where in repo:** `WebApp/api/config.php` (line 17), `WebApp/api/index.php` (line 7)
- **How to change:** Generate new 64-char hex string: `openssl rand -hex 32`
  Then set `API_SECRET` env var on server
- **Used by:** `api/index.php` — admin/debug API endpoint
- **What happens if wrong:** `api/index.php` returns 401 for all requests; no impact on
  normal app functionality

### Secret 3: `APP_HMAC_SECRET` (Android app ↔ server handshake)
- **Where in repo:** `app/src/main/java/com/snote/domezos/data/BackendClient.kt:20`
  (→ `BuildConfig.HMAC_SECRET`), `WebApp/api/encrypt_handshake.php:34`
- **How to change:** Generate new random string, set `APP_HMAC_SECRET` env var on server,
  set in `gradle.properties` (gitignored) → feeds into `BuildConfig.HMAC_SECRET`
- **Used by:** `encrypt_handshake.php` verifies HMAC signature on every app request
- **What happens if mismatch:** All app requests to `encrypt_handshake.php` fail with
  401 `invalid_signature`. Encrypt/decrypt still work (they don't use this endpoint).
  Only premium status checks and device registration break.

### Secret 4: `PREMIUM_API_KEY`
- **Where in repo:** `BackendClient.kt:77` (→ `BuildConfig.PREMIUM_API_KEY`),
  `WebApp/api/activate_premium.php:12`, `WebApp/api/deactivate_premium.php:8`
- **How to change:** Generate new random string, set `PREMIUM_API_KEY` env var on server,
  set in `gradle.properties`
- **Used by:** `activate_premium.php` and `deactivate_premium.php` verify this key before
  modifying premium status
- **What happens if mismatch:** Premium activation/deactivation fails with 401.
  Users cannot purchase or deactivate premium. Free functionality unaffected.

### Secret 5: `PREMIUM_HMAC_SECRET` (PayPal payment verification)
- **Where in repo:** `WebApp/api/premium_status.php:11` (`PREMIUM_SET_SECRET`),
  `WebApp/api/android_be_encrypt.php:37` (`$secret`)
- **How to change:** Generate new random string, set `PREMIUM_HMAC_SECRET` env var on server
- **Used by:** Server-side signature verification of PayPal payment callbacks
- **What happens if wrong:** Premium activation via PayPal fails. The PayPal webhook
  signature verification will mismatch. Users pay but don't get premium.

### Secret 6: `HONEYPOT_PASS` + `HONEYPOT_FILE`
- **Where in repo:** `WebApp/api/msg_store.php:8-9`
- **Where on server:** Same file
- **How to change:** Set `HONEYPOT_FILE` and `HONEYPOT_PASS` env vars on server
- **Used by:** Anti-enumeration decoy — when a message alias is not found, returns a
  plausible-looking fake payload with the honeypot password
- **What happens if wrong:** Not-found messages would use the wrong password (users get
  "decryption failed" instead of a decoy). Low security impact.

### Secret 7: PayPal credentials
- **Where in repo (NOT included — excluded as snote.fun service):**
  `links/paypal-payment.php`, `links/donate.php`, `assets/paypal_init.php`,
  `assets/paypal_webhook.php`

  Wait — `paypal-payment.php`, `donate.php` are in the `links/` directory and are EXCLUDED
  (snote.fun service). But `paypal_init.php` and `paypal_webhook.php` are in `assets/` —
  these ARE the dw Secret Notes PayPal integration for premium subscriptions.

  **Resolution:** Include `assets/paypal_init.php` and `assets/paypal_webhook.php` (sanitized).
  These are used by the dw Secret Notes premium flow. The `links/` PayPal files
  (`paypal-payment.php`, `donate.php`) are for the snote.fun website and are EXCLUDED.

- **`PAYPAL_CLIENT_ID`, `PAYPAL_CLIENT_SECRET`:** Where in repo:
  `WebApp/assets/paypal_init.php:11-12`, `WebApp/assets/paypal_webhook.php:7-8`
  How to change: Regenerate in PayPal Developer Dashboard. Set as env vars on server.
  What happens if wrong: PayPal OAuth flow fails. Premium subscriptions won't work.

- **`PAYPAL_PLAN_ID`:** Only in `links/paypal-payment.php` (EXCLUDED — not in repo).
  The dw Secret Notes Android app uses Play Billing, not PayPal directly. PayPal webhooks
  on the server side handle subscription renewals. The plan ID is in the excluded file.

- **`PAYPAL_WEBHOOK_ID`:** Only in `links/paypal-payment.php` (EXCLUDED) and
  `assets/paypal_webhook.php:9` (INCLUDED). Same value. How to change: Recreate webhook
  in PayPal Developer Dashboard.

### Secret 8: Firebase service account (snote.fun)
- **Where:** `links/firebase-adminsdk.json` — EXCLUDED (snote.fun service, not dw Secret Notes)
- The dw Secret Notes WebApp only needs `links/firebase-config.js` (public config) if
  using snote.fun short links. Since we're excluding snote.fun, the WebApp's short-link
  resolution will only work against `snote.fun` directly (which has its own Firebase setup).

  **For self-hosted snote.fun:** If a user wants to run their own snote.fun, they need
  the `links/` directory files. This is out of scope — document it as a separate project.

### Secret 9: `snote-domezos-cloud-*.json` (GCP service account)
- **Where:** `WebApp/.well-known/snote-domezos-cloud-65f9fec00feb.json` — EXCLUDE
- **Reason:** Unused by any dw Secret Notes PHP code. Likely for a cloud function or
  CI/CD pipeline. Exclude. If needed, provide as `.example` template.

### Secret 10: `dothis.txt`
- **Where:** `WebApp/dothis.txt` — NEVER PUBLISH
- **Reason:** Contains a private admin message. Add to `.gitignore`.

## 6. How to Run on Another Server / Domain

### Prerequisites
- PHP 7.4+ with `pdo_mysql` extension
- MySQL 5.7+ or MariaDB 10.3+
- HTTPS (required for Web Crypto API)
- Write permissions: `WebApp/msges/` directory (for message storage), `WebApp/links/` (for short links)
- (Optional) Firebase project + PayPal developer account (for premium subscriptions)

### Step 1 — Upload files
1. Upload the entire `WebApp/` directory to your server's document root (or a
   subdirectory). All paths are relative.
2. Ensure `WebApp/msges/` is writable (`chmod 755` or `775`).
3. If using short links (premium feature), create an empty `WebApp/links/` directory
   with write permissions.

### Step 2 — Database setup
1. Create a MySQL database and user:
   ```sql
   CREATE DATABASE dw_secret_notes CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE USER 'dw_user'@'localhost' IDENTIFIED BY 'DeinStarkesPasswort123';
   GRANT ALL PRIVILEGES ON dw_secret_notes.* TO 'dw_user'@'localhost';
   FLUSH PRIVILEGES;
   ```
2. Import the schema:
   ```bash
   mysql dw_secret_notes < WebApp/database/schema.sql
   ```
3. Pre-populate app version info:
   ```sql
   INSERT INTO app_config (config_key, config_value) VALUES
   ('apk_version', '4.6'), ('apk_version_code', '43');
   ```

### Step 3 — Configure secrets
1. Copy `WebApp/.env.example` to `WebApp/.env`:
   ```bash
   cp WebApp/.env.example WebApp/.env
   ```
2. Edit `WebApp/.env` and fill in your real values (see §2 for variable names).
3. Configure your web server to load `.env` variables:
   - **Apache:** Add to `.htaccess`:
     ```apache
     SetEnv DB_HOST "database-hostname"
     SetEnv DB_NAME "db-name"
     # etc.
     ```
     Or use `php dotenv` library.
   - **nginx + PHP-FPM:** Set `env[DB_PASS] = ...` in `www.conf`.
   - **Shared hosting:** Often set via control panel "environment variables" or
     put values directly in `.env` and add a PHP loader at the top of `config.php`.

### Step 4 — Android app configuration
1. Install [Android Studio](https://developer.android.com/studio).
2. Build a Release keystore:
   ```bash
   keytool -genkeypair -alias dw-secret-notes -keyalg RSA -keysize 2048 -validity 3650 -keystore release.keystore
   ```
3. Configure signing in `app/build.gradle.kts`:
   ```kotlin
   buildTypes {
       release {
           signingConfig = signingConfigs.create("release") {
               storeFile = file("../release.keystore")
               storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
               keyAlias = "dw-secret-notes"
               keyPassword = System.getenv("KEY_PASSWORD") ?: ""
           }
           // ... existing release config
       }
   }
   ```
4. Create `gradle.properties` (gitignored) with your secrets:
   ```properties
   BACKEND_BASE_URL=https://deine-domain.de/api/
   ENCRYPT_ENDPOINT=https://deine-domain.de/api/android_be_encrypt.php
   DECRYPT_ENDPOINT=https://deine-domain.de/api/view_api.php
   HMAC_SECRET=<same as server's APP_HMAC_SECRET env var>
   PREMIUM_API_KEY=<same as server's PREMIUM_API_KEY env var>
   ```
5. Build: `./gradlew assembleRelease`

### Step 5 — Domain-specific files
1. **`assetlinks.json`:** Update with your app's package name (if changed) and new
   SHA256 fingerprints. Generate fingerprints:
   ```bash
   keytool -list -v -keystore release.keystore -alias dw-secret-notes
   ```
   Deploy to `https://deine-domain.de/.well-known/assetlinks.json`.

2. **Play Billing:** Update product IDs in Play Console to match your products.
   The app hardcodes `premium_tinyurl_30days` and `tipp_jar` — change in
   `BillingHelper.kt`.

3. **App icons:** Replace `app/src/main/res/drawable/app_icon.png` and
   `app/src/main/ic_launcher-playstore.png`.

### Step 6 — Testing
1. Start the WebApp in a browser — encrypt a message, verify it decrypts.
2. Build and install the Android app — encrypt, decrypt, verify the link works.
3. Test deep-link resolution: click a `?com=...&pass=...` link, verify auto-decrypt.

## 7. Android App Secret Remediation (in-repo changes only)

### Files to modify in this repo:

#### `app/src/main/java/com/snote/domezos/data/BackendClient.kt`
- Replace `private const val HMAC_SECRET = "change-me"` →
  `private val HMAC_SECRET = BuildConfig.HMAC_SECRET`
- Replace `private const val PREMIUM_API_KEY = "NMsePsV2026"` →
  `private val PREMIUM_API_KEY = BuildConfig.PREMIUM_API_KEY`
- Replace `internal var BASE_URL = "https://domezos-ware.org/api/"` →
  `internal var BASE_URL = BuildConfig.BACKEND_BASE_URL`
- Add: `import com.snote.domezos.BuildConfig`

**Test impact:**
- `BackendClientHmacTest.kt` — tests `hmacSha256()` function directly, not the constants. **No change needed.**
- `BackendClientNetworkTest.kt` — overrides `BackendClient.BASE_URL` via MockWebServer.
  Since `BASE_URL` becomes `BuildConfig.BACKEND_BASE_URL`, the test's `originalBaseUrl`
  should be updated to `BuildConfig.BACKEND_BASE_URL`. The test still works because it
  reassigns `BackendClient.BASE_URL`.

#### `app/src/main/java/com/snote/domezos/ui/components/SecretWebView.kt`
- Replace hardcoded endpoint URLs with `BuildConfig.*` values:
  ```kotlin
  private val ENCRYPT_ENDPOINT = BuildConfig.ENCRYPT_ENDPOINT
  private val DECRYPT_ENDPOINT = BuildConfig.DECRYPT_ENDPOINT
  private val SHORT_LINK_HOST = BuildConfig.SHORT_LINK_HOST
  ```
- Add: `import com.snote.domezos.BuildConfig`

#### `app/build.gradle.kts`
- Add `buildConfigField` entries in `defaultConfig` (see §2).

#### `gradle.properties`
- **Gitignore** this file (currently committed — must be removed from tracking).
- Create `gradle.properties.example` with placeholder values.

#### `.gitignore`
- Add `/gradle.properties` to prevent future commits.

### What does NOT change (local files unchanged):
- The live server at `/webapp/` keeps all real secrets hardcoded.
- The user's local Android build uses a local `gradle.properties` with real values.
- No secrets are rotated or changed on the live server.

## 8. .env.example Content

Create `WebApp/.env.example`:
```bash
# ── Database (MySQL) ──────────────────────────────────────────
# MySQL credentials from your hosting control panel
DB_HOST=localhost
DB_NAME=dw_secret_notes
DB_USER=dw_user
DB_PASS=DeinDatenbankPasswortHierEinfuegen

# ── Secret Notes API ──────────────────────────────────────────
# Admin/debug API key for api/index.php — generate with: openssl rand -hex 32
API_SECRET=DeinApiSecretHierEinfuegen

# ── Android App HMAC Secret ───────────────────────────────────
# Shared secret between Android app and server for encrypt_handshake.php
# MUST match the HMAC_SECRET in your local gradle.properties
APP_HMAC_SECRET=DeinHmacSecretHierEinfuegen

# ── Premium API Key ───────────────────────────────────────────
# API key for activate_premium.php and deactivate_premium.php
# MUST match PREMIUM_API_KEY in your local gradle.properties
PREMIUM_API_KEY=DeinPremiumApiKeyHierEinfuegen

# ── Premium PayPal Webhook HMAC Secret ────────────────────────
# Used by premium_status.php and android_be_encrypt.php to verify
# PayPal payment success callbacks
PREMIUM_HMAC_SECRET=DeinPremiumHmacSecretHierEinfuegen

# ── Honeypot (anti-enumeration) ───────────────────────────────
# Fake payload returned for non-existent message aliases
HONEYPOT_FILE=16.11.2025_18-03-49-200.txt
HONEYPOT_PASS=DeinHoneypotPasswortHierEinfuegen

# ── PayPal (optional — for Premium subscriptions) ─────────────
PAYPAL_CLIENT_ID=DeinPaypalClientIdHierEinfuegen
PAYPAL_CLIENT_SECRET=DeinPaypalClientSecretHierEinfuegen
PAYPAL_WEBHOOK_ID=DeinPaypalWebhookIdHierEinfuegen

# ── Firebase (optional — for snote.fun short links) ───────────
# Only needed if running your own snote.fun instance
FIREBASE_SA_FILE=/absolute/path/to/firebase-adminsdk.json
# OR set the entire JSON string as an env var:
# FIREBASE_SA_JSON={"type":"service_account","project_id":"...","private_key":"..."}

# ── Domain configuration ──────────────────────────────────────
APP_DOMAIN=domezos-ware.org    # your domain without https://
SHORT_LINK_DOMAIN=snote.fun    # your short-link domain
```

## 9. .gitignore Updates

Add these entries to `.gitignore`:

```gitignore
# ── Secrets ──────────────────────────────────────────────────
# Android app secrets (local, with real values)
/gradle.properties

# WebApp environment file (local, with real secrets)
WebApp/.env
WebApp/.env.local

# Firebase service account (local, with real key)
WebApp/links/firebase-adminsdk.json
WebApp/.well-known/snote-domezos-*.json

# ── User data (generated at runtime, never commit) ───────────
# Encrypted messages
WebApp/msges/*.txt
WebApp/msges/16.11.*  # honeypot wildcard

# Short-URL data
WebApp/links/*.txt
WebApp/links/tiny-*.txt
WebApp/links/._rate/
WebApp/links/webhook.log
WebApp/links/donations.log
WebApp/links/_last_req.log

# Private admin files
WebApp/dothis.txt

# ── Build artifacts ──────────────────────────────────────────
WebApp/database/schema.sql.bak  # (if locally generated)
```

**CRITICAL:** The `.env` file, `firebase-adminsdk.json`, and `gradle.properties` must
NEVER be committed. Verify with `git status` before pushing.

## 10. Git History Cleanup (Pre-Publication)

The following secrets are already in git history:
- `BackendClient.kt`: `HMAC_SECRET = "change-me"`, `PREMIUM_API_KEY = "NMsePsV2026"`

**After** rotating these secrets (see §4, Phase 0):

1. Install `git-filter-repo`:
   ```bash
   pip install git-filter-repo
   ```
2. Create a replacement rules file:
   ```
   # replace-secrets.txt
   REWRITE:==>==>==
   "change-me"==>***REDACTED_HMAC_SECRET***==>===BackendClient.kt
   "NMsePsV2026"==>***REDACTED_PREMIUM_API_KEY***==>===BackendClient.kt
   ```
3. Run scrubbing:
   ```bash
   git filter-repo --replace-text replace-secrets.txt --force
   ```
4. Force-push:
   ```bash
   git remote set-url origin git@github.com:domezos2024/dw-secret-notes.git
   git push --force-with-lease
   ```
5. **Notify all collaborators** — they must re-clone.

**NOTE:** Since this is a single-user repo (no PRs or branches visible), force-push is safe.
Do this ONLY after secrets are rotated and the public-facing app uses new values.

## 11. File Dependency Graph

```
Android App (Kotlin)
├─ SecretWebView.kt
│   ├─ ENCRYPT_ENDPOINT = /api/android_be_encrypt.php
│   │   └─ (serves HTML+JS with client-side AES crypto)
│   │       ├─ embedded JS → POST /api/msg_store.php?action=save
│   │       ├─ embedded JS → GET  /api/premium_status.php?action=check
│   │       └─ embedded JS → GET  /save_link.php (premium short links)
│   └─ DECRYPT_ENDPOINT = /api/view_api.php
│       └─ (serves HTML+JS with client-side AES crypto)
│           └─ embedded JS → GET /api/msg_store.php?action=get|unlink
│
├─ BackendClient.kt
│   ├─ performHandshake() → POST /api/encrypt_handshake.php (HMAC-signed)
│   │   └─ encrypt_handshake.php → requires assets/db.php (MySQL)
│   ├─ activatePremium() → POST /api/activate_premium.php (API key)
│   │   └─ activate_premium.php → requires assets/db.php
│   └─ deactivatePremium() → POST /api/deactivate_premium.php (API key)
│       └─ deactivate_premium.php → requires assets/db.php
│
└─ Premium check (in android_be_encrypt.php embedded JS)
    └─ GET /api/premium_status.php?action=check|register|set
        └─ premium_status.php → requires assets/db.php

WebApp (Browser)
├─ js/backend.js
│   └─ STORE_ENDPOINT = /api/msg_store.php (save/get/unlink)
│       └─ msg_store.php → requires api/config.php (MESSAGES_DIR constant)
│   ├─ js/view.js → decrypt flow (calls backend.js)
│   ├─ js/main.js → encrypt flow (calls backend.js)
│   └─ js/aliasParser.js → resolves snote.fun short links
│       └─ snote.fun/?link=<alias> → links/index.php (EXCLUDED)
│
├─ msges/view.php (already in repo)
│   └─ loads js/view.js
│
└─ .htaccess, favicon.ico, manifest.json, sw.js, robots.txt, sitemap.xml
    (all served from webapp root)
```

**Critical path for core messaging:**
`index.html` → `js/main.js` → `js/backend.js` → `api/msg_store.php` → `api/config.php`

**Critical path for Android app:**
`SecretWebView` → `api/android_be_encrypt.php` → (embedded JS) → `api/msg_store.php`

## 12. Verification Checklist

Before switching the repo to public:

- [ ] All PHP files use `getenv()` instead of hardcoded secrets
- [ ] `firebase-adminsdk.json` is NOT in the repo (`.gitignore` verified)
- [ ] `WebApp/.env` is NOT in the repo (`.gitignore` verified)
- [ ] `gradle.properties` with real values is NOT in the repo (`.gitignore` verified)
- [ ] `BackendClient.kt` uses `BuildConfig.*` instead of hardcoded secrets
- [ ] `SecretWebView.kt` uses `BuildConfig.*` for endpoints
- [ ] `.env.example` lists all env vars with descriptions
- [ ] `gradle.properties.example` lists all build config vars
- [ ] `WebApp/database/schema.sql` contains complete DB schema
- [ ] `WebApp/database/README.md` has MySQL setup instructions
- [ ] `README-security.md` documents the threat model
- [ ] `WebApp/README.md` documents deployment + domain configuration
- [ ] `dothis.txt` is NOT committed
- [ ] No `*.txt` files in `WebApp/msges/` are committed
- [ ] No `*.txt` files in `WebApp/links/` are committed
- [ ] No `*.log` files are committed
- [ ] `trufflehog` or `git-secrets` scan passes clean
- [ ] Git history scrubbed of old secret values
- [ ] All live-server secrets rotated
