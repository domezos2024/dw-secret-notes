# dw Secret Notes

[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-26%2B-brightgreen.svg?logo=android)](https://play.google.com/store/apps/details?id=com.snote.domezos)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Play Store](https://img.shields.io/badge/Play%20Store-Download-blue?logo=googleplay)](https://play.google.com/store/apps/details?id=com.snote.domezos)
[![GitHub Release](https://img.shields.io/github/v/release/domezos2024/dw-secret-notes)](https://github.com/domezos2024/dw-secret-notes/releases/latest)
[![Download APK](https://img.shields.io/badge/Download-APK-brightgreen?logo=android)](https://github.com/domezos2024/dw-secret-notes/releases/latest)

Ende-zu-Ende-verschlüsselte Nachrichten (Text oder Bild), die sich nach einmaligem Lesen selbst zerstören. Nachricht schreiben → Link teilen → Empfänger liest einmal → Server löscht sie unwiderruflich. Kein Account nötig.

---

## Screenshots

| Verschlüsseln | Link erzeugt | Entschlüsseln (60s-Countdown) | Dunkles Theme |
|---|---|---|---|
| ![Encrypt](docs/screenshots/main-encrypt.png) | ![Link](docs/screenshots/link-generated.png) | ![Decrypt](docs/screenshots/decrypt-countdown.png) | ![Dark Theme](docs/screenshots/theme-dark.png) |

---

## Beim ersten Start

**Sprachauswahl** (15 Sprachen) erscheint. Tippen und bestätigen. Später jederzeit über Menü → **Language** ändern.

---

## Hauptbildschirm

Zwei Bereiche: oben **Versenden**, unten **Lesen**.

### Nachricht versenden

- **Textfeld** — Geheimtext eingeben.
- **Bild anhängen** (optional) — Foto aus Galerie wählen. Vorschau erscheint, ✕ entfernt es.
- **Encrypt** — Verschlüsselt und lädt hoch. Nur verschlüsselter Inhalt verlässt das Gerät.
- Danach: **Copy** (Link kopieren) · **Share** (Teilen-Menü) · **New Message** (neu starten).

### Nachricht lesen

- **Link or alias** — empfangenen Link einfügen. Automatische Kurzlink-Erzeugung war an Premium gekoppelt und wurde entfernt — Encrypt liefert nur noch den langen `view.php`-Link. Wer einen kürzeren Link will, kürzt ihn manuell über die externe Seite [snote.fun/tinyURL.html](https://snote.fun/tinyURL.html) (Menüpunkt **TinyURL**).
- **Decrypt** — ruft Nachricht ab. Nicht gefunden → rote Meldung.
- Nach Entschlüsselung: Text/Bild mit **60-Sekunden-Countdown**. Server-Kopie ist bereits beim Öffnen gelöscht.
- **Read another message** — Ansicht leeren.

---

## Menü (⋮ oben rechts)

| Menüpunkt | Funktion |
|---|---|
| Choose Theme | 15 Farbdesigns, sofort aktiv |
| Language | Sprache wechseln |
| TinyURL | Infos zum domezos-ware.com-Linkverkürzer |
| Help | FAQ & Funktionsweise |
| Info | Version, Entwickler, Lizenz |

---

## Homescreen-Widgets

Langer Druck auf Homescreen → Widgets:
- **Schnell-Verschlüsseln** — Nachricht direkt vom Homescreen eingeben und Link erhalten.
- **Launcher** — App-Symbol zum direkten Öffnen.

---

## Sicherheit

- **AES-256-Verschlüsselung** — clientseitig, bevor die Nachricht das Gerät verlässt.
- Server speichert nur verschlüsselten Inhalt, niemals Klartext.
- Beim Öffnen wird die Nachricht **sofort und unwiderruflich gelöscht** — kein zweites Lesen, kein Backup.
- Kostenlos · kein Account · keine In-App-Käufe.

---

## Changelog

### v5.0.1 — 20.08.2026

- **Android App Links verifiziert** — Deep Links öffnen zuverlässig auf allen Android-Versionen (`.well-known/assetlinks.json`)
- **Service Worker** zur WebApp hinzugefügt
- **SEO:** `robots.txt`, `sitemap.xml`, Meta-Tags, Canonical-URLs, Open Graph, JSON-LD
- **Kurzlinks:** Auto-Erzeugung entfernt — Encrypt erzeugt immer den langen `view.php`-Link; manuelles Kürzen über [snote.fun/tinyURL.html](https://snote.fun/tinyURL.html)
- **TinyURL-Screen** Layout überarbeitet
- **Build:** `compileSdk 37`, `targetSdk 37`, Kotlin 2.2.10, AGP 9.3.1, Java 21

---

*Website: [domezos-ware.com](https://domezos-ware.com)*
