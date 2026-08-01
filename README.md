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
   - [Premium](#premium)
   - [TinyURL](#tinyurl)
   - [Language](#language)
   - [Themes](#themes)
   - [Help & Info](#help--info)
4. [Deep Links & Sharing](#deep-links--sharing)
5. [Security](#security)
6. [Premium — Purchase Details](#premium--purchase-details)
7. [Supported Languages](#supported-languages)
8. [Technical Overview](#technical-overview)
9. [Build Variant](#build-variant)

---

## Features

| Feature | Free | Premium |
|---|:---:|:---:|
| Encrypt messages with AES 256-bit | ✅ | ✅ |
| Self-destructing links (read once) | ✅ | ✅ |
| Share links via any app | ✅ | ✅ |
| Receive & decrypt messages | ✅ | ✅ |
| Long-form encrypted link | ✅ | ✅ |
| Short 5-digit link (snote.fun/XXXXX) | — | ✅ |
| Alias display after encryption | — | ✅ |
| 15 visual themes | ✅ | ✅ |
| 15 languages | ✅ | ✅ |

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

A rotating banner at the bottom promotes Premium features (or shows motivational messages for existing Premium users). Tapping the banner navigates to the Premium screen.

---

### Encrypt a Message

1. Tap the text field at the top and type your secret message.
2. Tap **Encrypt**.
3. Wait a moment while the message is encrypted and uploaded.
4. After success:
   - The generated link appears in a read-only field.
   - **Copy** — copies the link to the clipboard and clears the form.
   - **Share** — opens the system share sheet so you can send the link via any app.
   - If you are a Premium user, your short 5-digit alias is also shown and automatically copied.
5. Tap **New Message** to start over.

> The link can only be decrypted inside dw Secret Notes. Once opened and decrypted, it is gone forever.

---

### Decrypt a Message

1. Paste the received link or alias into the **Link or alias** field.

   Accepted formats:

   | Format | Example |
   |---|---|
   | Full URL | `https://domezos-ware.org/api/...?com=XXXXX` |
   | Short URL | `https://snote.fun?link=XXXXX` |
   | Short path | `snote.fun/XXXXX` |
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

### Premium

Premium unlocks **short 5-digit snote.fun links** and the alias display after encryption.

**To purchase:**

1. Open the menu (⋮ top right) → **Premium**.
2. Review the offer details (price, duration, terms).
3. Tap **Get Premium Now**.
4. Complete the Google Play in-app purchase flow.
5. After a successful purchase, the screen shows **PREMIUM ACTIVE** and all Premium features are immediately enabled.

**To restore a previous purchase:**

Tap **Restore Purchase** on the Premium screen. The app queries Google Play for existing purchases linked to your account.

---

### TinyURL

Menu (⋮) → **TinyURL** shows information about the snote.fun URL shortener service.

- Shorten any long URL to a 5-character alias at snote.fun.
- Premium features: eternal links, custom aliases, password protection, QR codes, dashboard, max-views limit, statistics.
- Tap **Visit Website** to open the service in your browser.

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

- `https://domezos-ware.org/...` — standard encrypted links
- `https://snote.fun/...` — short Premium links

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
| Request authentication | Per-device token + HMAC-SHA256 signed API requests |

**Known limitations:**

- The recipient must use dw Secret Notes to decrypt. The link cannot be opened in a browser.
- If the connection drops mid-decryption, the server may delete the message before it is displayed. It cannot be recovered.

---

## Premium — Purchase Details

> As required by Google Play subscription policy, all terms are also shown on the Premium screen inside the app.

| Item | Detail |
|---|---|
| Price | €1.79 |
| Access period | 30 days from date of purchase |
| Auto-renewal | **No** — this is a one-time purchase. It does not renew automatically. |
| Required to use the app | **No** — core features (encrypt, decrypt) are free and unlimited. |
| Restore | Tap "Restore Purchase" on the Premium screen. |

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
| Billing | Google Play Billing Library 7.1.1 |
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
├── billing/
│   └── BillingHelper.kt            Google Play Billing integration
├── data/
│   ├── BackendClient.kt            HMAC-signed API handshake
│   ├── LocaleManager.kt            Locale context creation
│   └── Prefs.kt                    SharedPreferences wrapper
├── navigation/
│   ├── AppNavigation.kt            NavHost with all routes
│   └── Screen.kt                   Sealed class for type-safe routes
└── ui/
    ├── components/
    │   ├── AppTopBar.kt             Top bar with navigation menu and theme picker
    │   └── SecretWebView.kt         Encryption / decryption WebView bridge
    ├── screens/
    │   ├── MainScreen.kt            Encrypt + Decrypt home screen
    │   ├── PremiumScreen.kt         Premium purchase screen
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

The project ships a single build variant, distributed exclusively through the Google Play Store. Premium purchases use Google Play in-app purchase (Billing).

---

*© 2026 snote.fun — Michael Bergfeld. All rights reserved.*
