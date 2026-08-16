# dw Secret Notes

**Encrypted self-destructing messages — read once, gone forever.**

dw Secret Notes lets you send end-to-end encrypted messages via a simple shareable link. The recipient reads the message once inside the app; immediately after, it is permanently and irrevocably deleted from the server. No account required for basic use.

---

## Table of Contents

1. [Features](#features)
2. [How It Works](#how-it-works)
3. [Screens & Usage](#screens--usage)
   - [Home Screen](#home-screen)
   - [Encrypt a Message](#encrypt-a-message)
   - [Decrypt a Message](#decrypt-a-message)
   - [TinyURL](#tinyurl)
   - [Language](#language)
   - [Themes](#themes)
   - [Help & Info](#help--info)
4. [Deep Links & Sharing](#deep-links--sharing)
5. [Security](#security)
6. [Supported Languages](#supported-languages)
7. [Technical Overview](#technical-overview)
8. [Build Variant](#build-variant)
9. [Public Backend API](#public-backend-api)
10. [License](#license)

---

## Features

- Encrypt messages with AES 256-bit
- Self-destructing links (read once)
- Share links via any app
- Receive & decrypt messages
- Long-form encrypted link
- 15 visual themes
- 15 languages

---

## How It Works

```
Sender                          Server                        Recipient
  │                               │                               │
  │  1. Type secret message       │                               │
  │  2. Tap Encrypt               │                               │
  │──────── AES 256-bit ─────────▶│  Stores encrypted ciphertext  │
  │◀──────── link / alias ────────│                               │
  │                               │                               │
  │  3. Share the link            │                               │
  │───────────────────────────────────────────────────────────────▶│
  │                               │                               │
  │                               │◀──────── Decrypt request ─────│
  │                               │──────── Plaintext ───────────▶│
  │                               │  Deletes entry immediately    │
  │                               │                               │
  │                               │  Message gone forever ✓       │
```

No plaintext ever leaves your device before encryption. The server only ever sees ciphertext. After the first successful decryption, the entry is deleted — there is no backup.

---

## Screens & Usage

### Home Screen

The home screen is divided into two sections separated by a horizontal divider:

- **Top half — Encrypt:** Compose and encrypt a new secret message.
- **Bottom half — Decrypt:** Paste a link or alias to read a message sent to you.

A rotating banner at the bottom highlights security features of the app (encryption, self-destruction, short links).

---

### Encrypt a Message

1. Tap the text field at the top and type your secret message.
2. Tap **Encrypt**.
3. Wait a moment while the message is encrypted and uploaded.
4. After success:
   - The generated link appears in a read-only field.
   - **Copy** — copies the link to the clipboard and clears the form.
   - **Share** — opens the system share sheet so you can send the link via any app.
5. Tap **New Message** to start over.

> The link can only be decrypted inside dw Secret Notes. Once opened and decrypted, it is gone forever.

---

### Decrypt a Message

1. Paste the received link or alias into the **Link or alias** field.

   Accepted formats:

   | Format | Example |
   |---|---|
   | Full URL | `https://domezos-ware.com/api/...?com=XXXXX` |
   | Short URL | `https://domezos-ware.com?link=XXXXX` |
   | Short path | `domezos-ware.com/XXXXX` |
   | 5-char alias | `AB1C2` |
   | Password-protected | `XXXXX\|password` |

2. Tap **Decrypt**.
3. If the message exists, it is shown in a highlighted box.
4. A **60-second countdown** starts. After it expires, the local copy is wiped from the screen.
5. Tap **Read another message** to reset.

**Error states:**

| Error | Meaning |
|---|---|
| *Message not found or already deleted* | The link was already opened, never existed, or the alias is wrong. |
| *Decryption failed. Wrong key?* | The message exists but the passphrase does not match. |

> **Important:** The message is deleted server-side on first decryption. If you close the app before the countdown ends, the server copy is already gone — you cannot retrieve it again.

---

### TinyURL

Menu (⋮) → **TinyURL** shows information about the domezos-ware.com URL shortener service.

- Shorten any long URL to a 5-character alias at domezos-ware.com.
- Optional tiers (without account / with account / paid) exist on the shortener website itself —
  unrelated to this app's own billing, which was removed (see below).
- Tap **Open domezos-ware.com/tinyURL** to open the service in your browser (this specific page is
  still hosted on `snote.fun`, verified live — everything else is on `domezos-ware.com`).

---

### Language

On first launch the app shows a mandatory language picker. You can change the language at any time:

1. Menu (⋮) → **Language**.
2. Scroll and tap a language — the UI preview updates live.
3. Tap **Confirm**. The app restarts to apply the new locale.

---

### Themes

Menu (⋮) → palette icon → select a theme. 15 themes are available:

| Theme | Style |
|---|---|
| Classic Blue | Dark navy, cyan primary, gold secondary |
| Midnight Purple | Material You purple on deep black |
| Forest Green | Deep forest greens |
| Ocean Deep | Deep ocean blues |
| Cyberpunk Neon | High-contrast neon yellow/red on black |
| Dracula Dark | Classic Dracula palette |
| Sunset Orange | Warm oranges on charcoal |
| Nordic Ice | Nord-inspired blue-grey |
| Matrix Green | Green on black |
| Sakura Dream | Soft pinks on deep red |
| Golden Age | Gold on near-black |
| Ruby Red | Deep reds and pink |
| Electric Violet | Vivid violet on deep purple |
| Ghost Gray | Monochrome grey |
| Solarized Dark | Classic Solarized palette |

The chosen theme is persisted across restarts.

---

### Help & Info

- **Help** (menu → Help): How the app works, the security model, and FAQ.
- **Info** (menu → Info): App version, developer, website, and license.

---

## Deep Links & Sharing

The app registers as a handler for the following URL patterns:

- `https://domezos-ware.com/msges/view.php?...` — standard encrypted links
- `https://domezos-ware.com?link=...` — short links

Tapping a matching link anywhere on your device (browser, chat, email) opens the app and auto-decrypts the message.

**Share-to-app:** You can also share a link from another app using the system share sheet — select dw Secret Notes and the link is extracted and decrypted automatically.

**Password-protected links** carry the password in the URL's `pass=` query parameter. It is parsed automatically; you do not need to enter it manually.

---

## Security

| Property | Detail |
|---|---|
| Encryption algorithm | AES 256-bit CBC |
| Storage | Ciphertext only — no plaintext is ever stored |
| Deletion policy | Immediate and permanent on first successful decryption |
| Screenshot protection | `FLAG_SECURE` — app hidden in Recents, screenshots blocked |
| Transport | HTTPS only |
| Request authentication | Backend API requires an API key (`X-API-Key` header or `apikey` param) — see [Public Backend API](#public-backend-api) |

**Known limitations:**

- The recipient must use dw Secret Notes to decrypt. The link cannot be opened in a browser.
- If the connection drops mid-decryption, the server may delete the message before it is displayed. It cannot be recovered.

---

## Supported Languages

English · Deutsch · Español · 中文 (简体) · हिन्दी · العربية · Português · বাংলা · Русский · 日本語 · Français · اردو · Indonesia · 한국어 · Italiano

---

## Technical Overview

| Component | Technology |
|---|---|
| Language | Kotlin |
| UI framework | Jetpack Compose + Material 3 |
| Navigation | Compose Navigation |
| Encryption bridge | WebView JavaScript interface → server-side PHP API |
| Persistence | SharedPreferences |
| Deep links | Android Intent filters + URI parsing |
| Locale | `createConfigurationContext` per-request |
| Splash screen | AndroidX SplashScreen |
| Security | `FLAG_SECURE` on the Activity window |

**Project structure:**

```
app/src/main/java/com/snote/domezos/
├── MainActivity.kt                 Entry point, deep link handling
├── DwApplication.kt                Locale application at startup
├── data/
│   ├── Backend.kt                  Backend host + first-party API key (single source of truth)
│   ├── ImageUtils.kt                Bitmap scaling / base64 helpers
│   ├── LocaleManager.kt             Locale context creation
│   └── Prefs.kt                     SharedPreferences wrapper
├── navigation/
│   ├── AppNavigation.kt             NavHost with all routes
│   └── Screen.kt                    Sealed class for type-safe routes
├── util/
│   └── ContextExt.kt                Activity-lookup extension
├── widget/
│   ├── SecretWidgetProvider.kt      Home-screen quick-capture widget
│   ├── LauncherWidgetProvider.kt    Launcher-icon widget
│   ├── WidgetEncryptActivity.kt     Transparent capture activity for widgets
│   └── WidgetRefresher.kt           Pushes theme/state updates to widgets
└── ui/
    ├── components/
    │   ├── AppTopBar.kt             Top bar with navigation menu and theme picker
    │   └── SecretWebView.kt         Encryption / decryption WebView bridge
    ├── screens/
    │   ├── MainScreen.kt            Encrypt + Decrypt home screen
    │   ├── TinyUrlScreen.kt         TinyURL info screen
    │   ├── LanguageScreen.kt        Language picker
    │   ├── HelpScreen.kt            Help & FAQ
    │   └── InfoScreen.kt            About screen
    └── theme/
        ├── AppThemes.kt             15 theme configurations
        ├── Color.kt                 Color palette
        ├── Theme.kt                 MaterialTheme wrapper
        └── Type.kt                  Typography scale
```

---

## Build Variant

The project ships a single build variant, distributed exclusively through the Google Play Store. There is no in-app purchasing or billing — the app is fully free.

---

## Public Backend API

The backend (`WebApp/api/`) can be used by other apps/clients, not just this Android app:

- Every request needs a valid API key — send it as header `X-API-Key: <key>` (preferred) or,
  where a caller can't set custom headers, as a request param `apikey=<key>`.
- Keys are issued manually for now (open an issue/PR). Rate limits are tiered
  (`free` / `trusted` / `internal`), conservatively sized for standard shared hosting — see
  `WebApp/api/auth.php` for the exact numbers and `WebApp/api/data/keys.example.php` for the
  key-file format.
- Endpoints and the crypto protocol are documented in [`WebApp/README.md`](WebApp/README.md).

---

## License

MIT — see [LICENSE](LICENSE). This covers the Android app and the `WebApp/` client; the code is
free to use, modify, and redistribute.

---

*© 2026 domezos-ware.com — Michael Bergfeld*
