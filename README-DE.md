# dw Secret Notes

**dw Secret Notes** ist eine Android-App, mit der du geheime Nachrichten (Text oder Bilder) verschicken kannst, die sich **nach dem einmaligen Lesen automatisch selbst zerstören**. Du schreibst eine Nachricht, die App verschlüsselt sie und gibt dir dafür einen Link. Diesen Link schickst du an jemanden (WhatsApp, E-Mail, SMS — egal wie). Die andere Person öffnet den Link, liest die Nachricht genau einmal, und danach wird sie unwiderruflich vom Server gelöscht. Niemand — nicht einmal der Entwickler der App — kann sie danach noch einmal lesen.

Stell es dir vor wie eine "Diese Nachricht zerstört sich selbst"-Notiz aus einem Spionagefilm — nur ganz real und alltagstauglich: um ein Passwort, einen privaten Gedanken oder ein sensibles Foto zu teilen, ohne dass irgendwo eine Spur zurückbleibt.

---

## 1. Beim ersten Start: Sprache auswählen

Wenn du die App zum allerersten Mal öffnest, siehst du den **Sprachauswahl-Bildschirm**. Das ist eine einfache Liste mit 15 Sprachen (Englisch, Deutsch, Spanisch, Chinesisch, Hindi, Arabisch, Portugiesisch, Bengalisch, Russisch, Japanisch, Französisch, Urdu, Indonesisch, Koreanisch, Italienisch). Tippe auf die gewünschte Sprache und dann auf den Button unten ("Bestätigen" / "Weiter"), um deine Wahl zu speichern und die App zu betreten. Du kannst die Sprache später jederzeit wieder ändern (siehe Menü, Abschnitt 3).

---

## 2. Der Hauptbildschirm — hier passiert alles

Der Hauptbildschirm ist in zwei Hälften geteilt: **die obere Hälfte zum Versenden** eines Geheimnisses, **die untere Hälfte zum Lesen** eines Geheimnisses.

### Ein Geheimnis versenden (oberer Bereich)

- **Textfeld ("Enter your text")** — Hier gibst du die Nachricht ein, die geheim bleiben soll.
- **Button "Attach image (optional)" (Bild anhängen)** — Damit wählst du ein Foto aus der Galerie deines Handys aus. Du kannst ein Bild statt Text oder zusätzlich zum Text anhängen. Nach der Auswahl erscheint eine kleine Vorschau des Bildes neben dem Button; tippe auf das kleine ✕ darauf, um das Bild wieder zu entfernen.
- **Button "Encrypt" (Verschlüsseln)** — Sobald du etwas eingegeben (oder ein Bild angehängt) hast, tippst du auf diesen Button. Die App verschlüsselt deine Nachricht mit bankenüblicher AES-256-Verschlüsselung und lädt nur die verschlüsselte, unlesbare Version auf den Server hoch — dein Originaltext verlässt dein Handy niemals im lesbaren Klartext.

Nach dem Verschlüsseln zeigt der Bildschirm das Ergebnis:
- **Link-Feld** — der geheime Link, den du teilen musst. Es ist schreibgeschützt (du kannst dort nichts eintippen).
- **Button "Copy" (Kopieren)** — kopiert den Link in die Zwischenablage, damit du ihn überall einfügen kannst.
- **Button "Share" (Teilen)** — öffnet das normale Teilen-Menü deines Handys (WhatsApp, E-Mail, SMS usw.) mit dem bereits eingefügten Link.
- **Button "New Message" (Neue Nachricht)** — löscht alles und lässt dich ein weiteres Geheimnis verschlüsseln.

### Ein Geheimnis lesen (unterer Bereich)

- **Textfeld "Link or alias" (Link oder Alias)** — Füge hier den geheimen Link (oder nur den kurzen 5-stelligen Code) ein, den du erhalten hast.
- **Button "Decrypt" (Entschlüsseln)** — Tippe hier, um die geheime Nachricht abzurufen und zu entschlüsseln. Wurde die Nachricht bereits vorher gelesen (oder existierte sie nie), erscheint eine rote Warnung "nicht gefunden".

Nach erfolgreicher Entschlüsselung siehst du:
- **Den Text der geheimen Nachricht** und/oder **das Bild**, genau dieses eine Mal enthüllt. Tippe auf ein Bild, um es im Vollbild zu sehen; tippe irgendwo hin, um es wieder zu schließen.
- **Einen Countdown ("Diese Nachricht wird in 60 Sekunden gelöscht…")** — eine visuelle Erinnerung daran, dass dieser Inhalt vorübergehend ist und gleich aus deiner Ansicht verschwindet. Sie wurde **bereits in dem Moment, als du sie geöffnet hast, unwiderruflich vom Server gelöscht** — der Countdown auf dem Bildschirm räumt nur deine eigene Ansicht auf.
- **Button "Read another message" (Weitere Nachricht lesen)** — leert den Bildschirm, damit du einen anderen Link entschlüsseln kannst.

### Die Box am unteren Rand

Unter beiden Bereichen gibt es eine kleine hervorgehobene Box, die abwechselnd kurze Hinweise zu den Sicherheitsmerkmalen der App zeigt (Verschlüsselungsstärke, Selbstzerstörung usw.).

---

## 3. Das Menü (⋮-Symbol, oben rechts)

Tippe auf die drei Punkte oben rechts auf jedem Bildschirm, um ein Dropdown-Menü mit folgenden Optionen zu öffnen:

| Menüpunkt | Was er bewirkt |
|---|---|
| **Choose Theme (Design wählen)** | Öffnet ein zweites Menü mit 16 Farbdesigns (z. B. Classic Blue, Midnight Purple, Forest Green, Cyberpunk Neon, Dracula Dark, Dunkelmodus, Hellmodus und mehr). Tippe eines an, um sofort die ganze App umzufärben. |
| **Help (Hilfe)** | Öffnet den Hilfe- & FAQ-Bildschirm (siehe Abschnitt 4). |
| **Language (Sprache)** | Öffnet erneut die Sprachauswahl, damit du die Sprache der App jederzeit wechseln kannst. |
| **TinyURL** | Öffnet einen Bildschirm zum Verkürzen langer Web-Links (siehe Abschnitt 5). |
| **Info** | Öffnet den Über-die-App-Bildschirm mit Version, Entwickler und rechtlichen Infos (siehe Abschnitt 6). |

---

## 4. Hilfe-Bildschirm

Eine einfache FAQ-Seite, die in klarer Sprache erklärt:
- **Wie es funktioniert** — Nachricht eingeben, auf Verschlüsseln tippen, Link teilen; der Empfänger öffnet ihn, die Nachricht wird einmal entschlüsselt und danach unwiderruflich gelöscht.
- **Sicherheit** — alles wird mit AES 256-Bit verschlüsselt, demselben Standard, den Banken und Regierungen verwenden. Der Server speichert immer nur die verschlüsselte Version, dein Originaltext verlässt dein Handy nie.
- **FAQ-Fragen** wie: "Kann die Nachricht zweimal gelesen werden?" (Nein — sie zerstört sich nach dem ersten Lesen selbst), und "Kann eine gelöschte Nachricht wiederhergestellt werden?" (Nein, es gibt kein Backup).

---

## 5. TinyURL-Bildschirm

Dieser Bildschirm erklärt eine separate, verwandte Funktion: das Verkürzen jedes beliebigen langen Web-Links auf nur 5 Zeichen, gehostet auf der eigenen Website des Entwicklers (domezos-ware.com). Er zeigt:
- Eine kurze Beschreibung der Funktion.
- Infos zu den kostenlosen/registrierten/bezahlten Stufen der externen Kurzlink-Website selbst — unabhängig vom Bezahlmodell dieser App, das es nicht (mehr) gibt.
- Einen Button **"Öffnen"**, der dich zur domezos-ware.com-Website in deinem Handy-Browser bringt, um dort tatsächlich einen Kurzlink zu erstellen (dieser Teil findet außerhalb der App statt).

---

## 6. Info-Bildschirm (Über die App)

Ein einfacher Referenzbildschirm mit:
- **Version** — die genaue Versionsnummer der installierten App.
- **Entwickler** — Michael Bergfeld.
- **Website** — domezos-ware.com.
- **Lizenz** — Proprietär (d. h. der Code ist nicht Open Source).
- Ein kurzer Hinweis, der wiederholt, dass alles mit AES-256-Bit-Verschlüsselung geschützt ist und selbstzerstörende Links garantieren, dass deine Geheimnisse nach dem einmaligen Lesen für immer verschwinden.

---

## 7. Homescreen-Widgets

Du kannst zwei kleine Widgets zu deinem Homescreen hinzufügen (außerhalb der App selbst), indem du lange auf deinen Homescreen drückst und "Widgets" wählst:
- **Schnell-Verschlüsseln-Widget** — lässt dich direkt vom Homescreen aus eine Nachricht eintippen und einen Teilen-Link erhalten, ohne die App überhaupt vollständig zu öffnen.
- **Launcher-Widget** — ein einfaches Symbol, das beim Antippen einfach die App öffnet.

---

## 8. Warum ist das sicher? (einfach erklärt)

- Deine Nachricht wird mit **AES-256** verschlüsselt — derselben Verschlüsselungsstärke, die Banken und Regierungen verwenden — bevor sie überhaupt dein Handy verlässt.
- Der Server speichert immer nur die verschlüsselte, unlesbare Version.
- In dem Moment, in dem jemand die Nachricht öffnet und liest, wird sie **unwiderruflich und endgültig gelöscht** — es gibt keine Möglichkeit, sie ein zweites Mal zu lesen, und es existiert nirgendwo ein Backup.
- Die App ist komplett kostenlos, ohne Konto und ohne In-App-Käufe — es gibt nichts, worüber man dich nachverfolgen könnte.

---

*Website: domezos-ware.com*
