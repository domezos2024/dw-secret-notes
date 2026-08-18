# dw Secret Notes — Web App

Eine reine PHP/HTML/CSS/JS-Nachbildung der Android-App **dw Secret Notes**. Kein Build-Schritt, kein
Framework, kein Server-Code — dieser Ordner ist direkt auf jeden Webserver deploybar.

Wichtig, und anders als es die App-seitigen Kommentare vermuten lassen: die eigentliche
Ver-/Entschlüsselung passiert **nicht** serverseitig, sondern client-seitig per Web-Crypto-API
(AES-256-GCM, Schlüssel via PBKDF2). Der Server (`domezos-ware.com`) liefert auf
`android_be_encrypt.php`/`view_api.php` eine HTML-Seite mit eingebettetem `<script type="module">`,
das in der Android-WebView ausgeführt wird und dort verschlüsselt/entschlüsselt, bevor es das
Ergebnis über die `Android.*`-JS-Bridge zurückgibt. Diese Web-Version führt exakt dasselbe
Kryptografie-Protokoll direkt in `js/backend.js` aus (siehe unten) und spricht dafür die
darunterliegenden Speicher-Endpunkte (`msg_store.php`) direkt an, statt fremden Code aus dem Netz
zu laden und auszuführen.

## Deployment

1. Lade den kompletten Inhalt dieses Ordners (`index.html`, `help.html`, `info.html`, `css/`,
   `js/`, `i18n/`, `manifest.webapp.json`) auf deinen Webserver hoch — egal ob Domain-Root oder
   Unterordner, alle Pfade sind relativ.
2. Fertig. Es gibt keinen Build-Schritt, keine `package.json`, keine Server-seitige Logik, die auf
   deinem Server laufen müsste.

Live deployt unter `https://domezos-ware.com/index.html` (die bestehende `index.php` auf der
Domain-Wurzel bleibt bewusst unangetastet, siehe nächster Abschnitt).

## "Als App installieren" (PWA) — parallel zu index.php

`domezos-ware.com` bietet bereits über `index.php` + `/manifest.json` + `/sw.js` eine
installierbare App ("Zum Startbildschirm hinzufügen"). Diese Web-Version bekommt ihre **eigene**,
unabhängige Installierbarkeit, ohne die bestehende anzufassen:

- `manifest.webapp.json` (neu, eigene Datei — überschreibt/verändert die bestehende
  `/manifest.json` nicht) definiert `"id": "/index.html"` und `"start_url": "/index.html"`, sodass
  Browser dies als eigenständige, von der `index.php`-App unterscheidbare App behandeln
  (unterschiedliche `start_url`/`id` = unterschiedliche Installationsidentität, auch bei
  überlappendem `scope: "/"`).
- `index.html`, `help.html` und `info.html` verlinken alle auf `manifest.webapp.json` und
  registrieren denselben, bereits vorhandenen `/sw.js` (ein einfacher Passthrough-Service-Worker
  ohne app-spezifische Caching-Logik — unproblematisch, von beiden Apps geteilt zu werden).
- Icons werden von den bestehenden `/assets/app_icon_192.png` / `_512.png` wiederverwendet (gleiches
  Marken-Icon wie die `index.php`-App); `theme_color`/`background_color` sind auf die eigene
  Farbgebung (`#050D1F`, Classic-Theme-Hintergrund) gesetzt, damit sich Splash-Screen/Taskleiste
  optisch von der bestehenden App unterscheiden.
- Wer will, kann `manifest.webapp.json` einfach löschen bzw. die `<link rel="manifest">`-Zeilen
  entfernen, um die Installierbarkeit der Web-App-Version wieder auszuschalten — `index.php` und
  ihre App-Installation sind davon zu keinem Zeitpunkt betroffen.

## Backend-Protokoll (live gegen den Produktivserver verifiziert)

Die folgenden Endpunkte und das Kryptografie-Protokoll wurden am 08.08.2026 per echtem HTTP-Request
(über den httpListener-MCP-Server, nicht aus dieser Sandbox heraus — siehe unten) gegen
`domezos-ware.com` verifiziert, inklusive eines vollständigen
Verschlüsseln→Speichern→Abrufen→Entschlüsseln→Löschen-Testlaufs mit exakter Übereinstimmung von
Klartext vorher/nachher:

- **Speichern**: `POST https://domezos-ware.com/api/msg_store.php?action=save&ts=<Zeitstempel>`,
  JSON-Body `{"iv":[...], "data":[...], "imgIv":[...]?, "imgData":[...]?}` — `iv`/`data` sind die
  AES-256-GCM-verschlüsselten Bytes des UTF-8-Texts (12-Byte-IV), `imgIv`/`imgData` optional analog
  für ein angehängtes Bild. Der AES-Schlüssel wird per PBKDF2 aus einer zufällig generierten,
  16-Byte-"Passphrase" abgeleitet (`salt="salt"`, 100.000 Iterationen, SHA-256, 256-Bit-Schlüssel).
  Der Zeitstempel (Format `TT.MM.JJJJ_hh-mm-ss-fff`) dient als `com`-Alias; der resultierende Link
  ist `https://domezos-ware.com/msges/view.php?com=<Zeitstempel>&pass=<Passphrase>`.
- **Abrufen**: `GET https://domezos-ware.com/api/msg_store.php?action=get&com=<alias>` liefert
  `{"status": "ok"|"not_found", "pass_override": string|null, "payload": {...}}`. Ein nicht
  gefundener Alias liefert trotzdem einen plausibel aussehenden `payload` zurück (Anti-Enumeration-
  Täuschung) — entscheidend ist `status`. Entschlüsselt wird mit `pass_override ?? mitgegebenePass`;
  ein GCM-Auth-Tag-Fehler (falscher Schlüssel) wirft eine Exception.
- **Löschen (Self-Destruct)**: Nach erfolgreichem Entschlüsseln feuert die echte Seite
  `GET .../msg_store.php?action=unlink&com=<alias>` — diese Web-Version tut dasselbe
  (fire-and-forget), damit die Nachricht wie beworben nur einmal lesbar ist.
- **Kurzlinks** (`https://domezos-ware.com?link=<5-stelliger Alias>`): `domezos-ware.com` liefert
  eine Redirect-Seite mit bereits aufgelöstem
  `const targetUrl = "https://domezos-ware.com/msges/view.php?com=...&pass=...";` inline im HTML —
  `resolveShortLink()` in `js/backend.js` liest das per Regex aus, ohne das dortige Redirect-Skript
  selbst auszuführen. Die automatische Kurzlink-Erzeugung war früher an Premium gekoppelt; seit der
  Premium-Entfernung erzeugt der Encrypt-Flow nur noch den langen `view.php`-Link.
- `Access-Control-Allow-Origin: *` ist auf allen getesteten Endpunkten gesetzt — `fetch()` von einer
  beliebigen Hosting-Domain aus funktioniert also ohne CORS-Probleme.
- **API-Key + Rate-Limiting**: `android_be_encrypt.php`, `view_api.php` und `msg_store.php` verlangen
  seit dem Public-Repo-Vorbereitung einen gültigen Key, entweder als Header `X-API-Key: <key>` oder
  (WebView-Limitation, `postUrl()`/`loadUrl()` unterstützen keine Custom-Header) als Request-Param
  `apikey=<key>`. Ohne/mit ungültigem Key → `401`. Key-Verwaltung und Limits: siehe
  `api/auth.php` + `api/data/keys.example.php`; echte Keys leben in `api/data/keys.php`
  (gitignored, nie committen). Tiers: `internal` (App/Web-App, 120/Min · 50.000/Tag), `trusted`
  (30/Min · 5.000/Tag), `free` (Standard für externe Nutzer, 10/Min · 1.000/Tag) — konservativ
  dimensioniert für Standard-Shared-Hosting ohne dokumentierte Concurrency-Grenzen. Bei
  Überschreitung: `429` + `Retry-After`-Header. `free` wird formlos vergeben (Issue/PR); `trusted`
  und `internal` sind kostenpflichtig — Preis wird individuell per Kontakt vereinbart, da höhere
  Quotas auf dem einzelnen Shared-Hosting-Server echte Mehrkosten verursachen.

**Wichtig für zukünftige Sessions in diesem Sandbox-Environment**: Direkter Netzwerkzugriff (`curl`,
`fetch` aus dieser Sandbox heraus) auf `domezos-ware.com` ist blockiert (Proxy antwortet mit 403).
Das bedeutet aber **nicht**, dass eine Live-Verifikation unmöglich ist — der `httpListener`-
MCP-Server (falls in der Session verfügbar) führt HTTP-Requests auf einem echten Rechner mit
Internetzugang aus und wurde genau dafür genutzt, um dieses Protokoll zu verifizieren. Bei künftigen
Änderungen an `js/backend.js` diesen Weg nutzen, statt Verhalten unverifiziert zu lassen.

## Sprache hinzufügen

15 Sprachen sind bereits vorhanden (`i18n/{en,de,es,zh-CN,hi,ar,pt,bn,ru,ja,fr,ur,id,ko,it}.json`),
identisch zur Android-App. Um eine weitere Sprache zu ergänzen:

1. Kopiere `i18n/en.json` zu `i18n/<tag>.json` und übersetze die Werte.
2. Trage die Sprache in `LANGUAGES` in `js/i18n.js` ein (`tag`, `native`-Name, `english`-Name).
3. Fertig — der Sprachumschalter zeigt sie automatisch an. Fehlt eine Locale-Datei zur Laufzeit,
   fällt die App automatisch (mit Konsolen-Warnung) auf Englisch zurück.

Für Rechts-nach-links-Sprachen (aktuell Arabisch/Urdu) trägt `RTL_LANGS` in `js/i18n.js` den Tag
ein, damit `<html dir="rtl">` gesetzt wird.

## Theme hinzufügen

1. Füge in `css/themes.css` einen neuen `[data-theme="<id>"]`-Block mit den Custom Properties
   `--bg`, `--surface`, `--surface-2`, `--primary`, `--primary-contrast`, `--secondary`, `--text`,
   `--text-muted`, `--border`, `--danger`, `--success` hinzu.
2. Trage `{ id: "<id>", swatch: "#..." }` in `THEMES` in `js/theme.js` ein (die `swatch`-Farbe wird
   nur für den kleinen Farbpunkt im Theme-Picker verwendet).
3. Füge den Anzeigenamen als `theme_<id>` in jeder `i18n/*.json`-Datei hinzu.

## Bekannte Parity-Lücken zur Android-App

- **Redirect-Following beim Einfügen von Links**: Die Android-App folgt beim manuellen Einfügen
  eines nicht direkt auflösbaren Links bis zu 5 HTTP-Redirects und parst dann die finale URL erneut
  (`MainScreen.resolveLink`). Das ist im Browser über `fetch()` nicht zuverlässig nachbaubar
  (`redirect: "manual"` liefert aus Cross-Origin-Gründen keine lesbare `Location`), daher gibt
  `js/aliasParser.js` bei einem nicht direkt auflösbaren Link eine "ungültiger Link"-Meldung
  zurück, statt Redirects zu folgen.
- **Vollständige ICU-Pluralformen**: Einige Sprachen (z. B. Arabisch, Russisch) haben in der
  Android-App mehr als zwei Pluralformen (`few`/`many`/`zero`). Die Web-Version verwendet
  vereinfacht nur `_one`/`_other` pro Sprache.

## Verifikationsstand

**Bereits geprüft:**
- Backend-Protokoll (siehe oben) live gegen den Produktivserver verifiziert, inklusive vollständigem
  Encrypt→Decrypt-Roundtrip mit exakter Klartext-Übereinstimmung.
- Alias-/Link-Parsing (`parseAliasFromInput`) gegen Testfälle geprüft (5-stelliger Alias,
  `domezos-ware.com?link=`, `domezos-ware.com?com=&pass=`, `domezos-ware.com/<alias>`-Pfadform, ungültige Eingabe).
- Bildskalierung (`resizeImageToBase64`) gegen ein Testbild geprüft (2000×1000 → 1600×800, kein
  `data:`-Prefix im Ergebnis).
- UI/Theme/Sprach-Rendering (inkl. RTL für Arabisch) per headless Chromium geprüft: Tab-Umschaltung,
  Sprachwechsel, Themewechsel, alles fehlerfrei in der Konsole.

**Offener Punkt:** Ein vollständiger Encrypt/Decrypt-Roundtrip direkt *durch die UI dieser Web-App*
(Klick auf "Encrypt"/"Decrypt" im Browser) wurde noch nicht durchgespielt, nur das zugrunde liegende
Protokoll isoliert per Skript. Nach dem Deployment einmal die echte Seite im Browser testen.
