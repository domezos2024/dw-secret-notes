# dw Secret Notes

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

*Website: [domezos-ware.com](https://domezos-ware.com)*
