# dw Secret Notes — Web App

Eine reine HTML/CSS/JS-Nachbildung der Android-App **dw Secret Notes**. Kein Build-Schritt, kein
Framework, kein Server-Code — dieser Ordner ist direkt auf jeden Webserver deploybar.

Die App ist ein Thin-Client: die eigentliche Ver-/Entschlüsselung passiert komplett serverseitig
auf `domezos-ware.org`. Diese Web-Version spricht dieselben Backend-Endpunkte an wie die
Android-App und bildet denselben Encrypt/Decrypt-Flow, dieselben 15 Sprachen und dasselbe
Theme-System (17 Themes) nach.

## Deployment

1. Lade den kompletten Inhalt dieses Ordners (`index.html`, `help.html`, `info.html`, `css/`,
   `js/`, `i18n/`) auf deinen Webserver hoch — egal ob Domain-Root oder Unterordner, alle Pfade
   sind relativ.
2. Fertig. Es gibt keinen Build-Schritt, keine `package.json`, keine Server-seitige Logik, die auf
   deinem Server laufen müsste.

## ⚠️ Bekannte Einschränkung: Response-Format der Backend-Endpunkte

Das ist der wichtigste Punkt, den du nach dem Deployment prüfen solltest.

Die Android-App lädt die Backend-Antworten in einer WebView, die eine JavaScript-Bridge
(`Android.sendAnswer(...)`, `Android.sendImage(...)`, `Android.notifyDataReady({url})`)
bereitstellt. Das bedeutet: `android_be_encrypt.php` und `view_api.php` liefern vermutlich
serverseitig gerendertes HTML mit eingebettetem `<script>`, das genau diese Bridge-Funktionen
aufruft — kein sauberes JSON. Ein normaler Browser hat diese Bridge nicht.

`js/backend.js` geht deshalb zweistufig vor:

1. **Primärversuch**: `fetch()` gegen den Endpunkt. Ist die Antwort JSON, wird sie direkt geparst.
   Ist sie HTML (wahrscheinlicher), wird per Regex nach den Mustern gesucht, die die
   `Android.*`-Bridge-Aufrufe enthalten würden — die Seite verhält sich also wie eine "virtuelle
   WebView", die dieselben JS-Aufrufe ausliest statt sie auszuführen.
2. **Fallback bei CORS-Fehlern**: Schlägt `fetch()` fehl (typischerweise weil der Server keine
   `Access-Control-Allow-Origin`-Header für deine Domain sendet), öffnet die App die bestehende,
   serverseitig gerenderte Seite (`view.php?com=...` bzw. `snote.fun?link=...`) in einem neuen Tab.

**Dieses Sandbox-Environment hatte keinen Netzwerkzugriff auf `domezos-ware.org`/`snote.fun` und
konnte das reale Response-Format daher nicht verifizieren.** Bitte nach dem Deployment einmal
prüfen:

1. Öffne die deployte Seite, öffne die Browser-DevTools (Network-Tab).
2. Verschlüssele eine Testnachricht und entschlüssele sie wieder über einen zweiten Tab/Browser.
3. Schau dir die tatsächliche Response von `android_be_encrypt.php`/`view_api.php` an:
   - Kommt überhaupt eine Antwort an, oder blockiert CORS den Request? (Fehler in der Konsole wie
     `has been blocked by CORS policy`)
   - Ist die Antwort JSON oder HTML?
4. Passe bei Bedarf `RESPONSE_MODE` in `js/backend.js` an (`"auto"` | `"json"` |
   `"html-bridge-scrape"` | `"redirect"`) oder erweitere `parseBridgeResponse()`/
   `extractBridgePayload()` um das tatsächlich beobachtete Format.

Falls CORS den direkten Zugriff dauerhaft blockiert, ist der eingebaute Redirect-Fallback (neuer
Tab zur Original-Seite) die praktikable Lösung — genau wie die Android-WebView im Prinzip auch nur
eine eingebettete Browser-Ansicht der Original-Seite ist.

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
- **Premium/Tip**: Die Google-Play-Billing-Features (Premium-Freischaltung, Tip-Jar) sind bewusst
  nicht nachgebaut — es gibt kein Zahlungssystem im Web. `info.html` weist kurz darauf hin.
- **Vollständige ICU-Pluralformen**: Einige Sprachen (z. B. Arabisch, Russisch) haben in der
  Android-App mehr als zwei Pluralformen (`few`/`many`/`zero`). Die Web-Version verwendet
  vereinfacht nur `_one`/`_other` pro Sprache.

## Verifiziert vs. noch zu verifizieren

**Ohne Live-Deployment bereits geprüft/prüfbar:**
- Alias-/Link-Parsing (`parseAliasFromInput`) lässt sich direkt in der Browser-Konsole gegen
  Testfälle prüfen (5-stelliger Alias, `snote.fun?link=`, `domezos-ware.org?com=&pass=`, ...).
- Bildskalierung (`resizeImageToBase64`) lässt sich gegen ein Testbild prüfen (Ausgabe ≤1600px,
  kein `data:`-Prefix).
- Gesamtes UI/Theme/Sprach-Rendering funktioniert offline durch direktes Öffnen von `index.html`.

**Erst nach echtem Deployment vollständig verifizierbar:** siehe Abschnitt "Bekannte Einschränkung"
oben — das reale Backend-Response-Format und CORS-Verhalten.
