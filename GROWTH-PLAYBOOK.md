# Growth Playbook — dw Secret Notes

App: `com.snote.domezos` · [Play Store](https://play.google.com/store/apps/details?id=com.snote.domezos) · Web: domezos-ware.com
USP: E2E-verschlüsselte, selbstzerstörende Notizen **inkl. Bild-Versand** — Inhalt max. 60 Sek., dann unwiderruflich gelöscht.
Status: Live, kleine Nutzerbasis (~10 MAU).

> **Wichtig:** Bei Posts auf Fremdplattformen (Reddit, Foren, Discord, Product Hunt, AlternativeTo) immer **zuerst Text zur Freigabe zeigen**, nicht blind posten.

---

## 1. Reddit

**Subreddits:** r/privacy, r/opsec, r/androidapps, r/AndroidQuestions, r/de

**Post EN** (r/privacy / r/androidapps):
> **Title:** I built a self-destructing notes app with image support (Privnote alternative) — would love feedback
>
> Hey everyone, I got tired of Privnote-style tools not supporting images, so I built my own: snote.domezos Secret Notes (Android, free).
> - E2E encrypted note + image sharing
> - Recipient gets a one-time link, content shown max. 60 seconds, then permanently destroyed
> - No account needed, link or 5-char alias
> - 15 languages
>
> Small side project, currently very few users. Genuine feedback on security model or UX appreciated.
> Play Store: https://play.google.com/store/apps/details?id=com.snote.domezos

**Post DE** (r/de):
> **Titel:** Selbstzerstörende Notizen-App mit Bildversand gebaut (Privnote-Alternative) — Feedback willkommen
>
> E2E verschlüsselter Text- und Bildversand. Einmal-Link, Inhalt max. 60 Sek., danach unwiderruflich gelöscht. Kein Account, 15 Sprachen, kostenlos.
> Play Store: https://play.google.com/store/apps/details?id=com.snote.domezos

**Hinweis:** Subreddit-Regeln zu Selbstpromotion prüfen (Karma, Wochentag). Alternativ als Kommentar in bestehenden "What privacy tools do you use"-Threads einfügen.

---

## 2. AlternativeTo.net

Neuen Eintrag anlegen, als Alternative zu **Privnote** und **One-Time Secret** taggen.

> snote.domezos Secret Notes is a free, end-to-end encrypted app for sharing self-destructing notes and images. Unlike Privnote, it supports image attachments. Content shown max. 60 seconds before permanent deletion. No account required. 15 languages.

Tags: Privacy, Encryption, Self-destructing messages, Note sharing, Android

---

## 3. Product Hunt

**Tagline:** Self-destructing notes & images, gone in 60 seconds

**Beschreibung:**
> snote.domezos Secret Notes lets you send E2E encrypted notes and images that self-destruct after being viewed once — max. 60 seconds on screen, then gone forever. No account needed. Think Privnote, but with image support and 15 languages.

**Maker-Kommentar:**
> Built this because I couldn't find a Privnote-style tool that also handles images securely. Would love feedback on the security model, UX, or feature requests.

---

## 4. SEO-Blogartikel (domezos-ware.com)

**Ziel-Keywords:** "wie versende ich ein Passwort sicher", "selbstlöschende Nachricht senden", "Privnote Alternative mit Bild", "self-destructing message app"

**Artikel 1 (DE):** "Wie versende ich ein Passwort sicher? 5 Methoden im Vergleich"
- Methoden: Passwort-Manager, Signal, Selbstzerstörende-Notizen-Dienste, snote.domezos (+ Bild), Fazit + CTA

**Artikel 2 (EN):** "How to Send a Self-Destructing Message (With or Without Images)"
- Inhalt: Was diese Tools tun/nicht tun, Text-only-Alternativen, Bildversand-Use-Case, snote.domezos Schritt-für-Schritt, FAQ

---

## 5. TikTok / YouTube Shorts

**Hook (0:00–0:02):** "Diese Nachricht zerstört sich in 60 Sekunden selbst"

**Shot-Liste:**
1. App öffnen, Text/Bild eingeben
2. Link generieren, an zweites Gerät senden
3. Empfänger öffnet Link, Countdown läuft (60 → 0)
4. Inhalt verschwindet, "Nachricht gelöscht"-Screen
5. Text-Overlay: "Kostenlos · Keine Anmeldung · 15 Sprachen"
6. End-Card: App-Icon + Play-Store-Link

Caption: `So verschickst du ein Bild, das sich nach 60 Sekunden selbst löscht 🔒 #privacy #android #selfdestruct`

---

## 6. Security-Communities (Bitwarden, KeePass, r/Bitwarden, r/KeePass)

Antwort-Template für Threads à la "Wie teile ich ein Passwort sicher?":
> Falls es nicht dauerhaft in einem Passwort-Manager landen soll: Ich nutze für Einmal-Freigaben selbstzerstörende Notizen-Tools. Falls neben Text auch ein Screenshot/Bild nötig ist, hat snote.domezos das eingebaut — Inhalt verschwindet nach max. 60 Sekunden automatisch. Kostenlos, keine Anmeldung nötig.

Nur in passenden Threads posten — kein Cold-Posting.

---

## 7. In-App Share-Text optimieren

**Aufgabe in `MainScreen.kt` / `strings.xml`:** Share-Text um Play-Store-CTA erweitern, in alle 15 Sprachen übersetzen.

Beispiel:
```
Ich hab dir eine geheime Nachricht geschickt: {link}
Erstellt mit snote.domezos — kostenlose App für selbstzerstörende Notizen:
https://play.google.com/store/apps/details?id=com.snote.domezos
```

Jeder Link-Empfänger ist ein potenzieller neuer Nutzer — der am meisten unterschätzte Kanal.

---

## 8. Google Play Kategorie & "Ähnliche Apps"

- Kategorie: **Tools** oder **Produktivität** — recherchieren, wo Konkurrenz-Apps (Privnote, Vanish, Burn Note) gelistet sind.
- Kurzbeschreibung + Tags mit Kern-Keywords füllen: "self-destructing message", "secret note", "burn after reading" → Play-Algorithmus sortiert App in "Ähnliche Apps"-Sektion der Konkurrenten ein.
- Kostenfreier Traffic-Kanal, keine Anzeigen nötig.

---

## 9. ASO — Fertige Store-Texte je Sprache

**Befunde (geprüft 2026-07-12):**
- Privnote + Burn Note haben keine native Android-App → unbesetzte Keywords.
- Kein echter "selbstzerstörende Notiz per Link"-Konkurrent auf Play Store.
- Nächste Konkurrenz-App: **"Secret Message" (DTG Tech)** — Keywords: `AES-256`, `Self-Destruct Timer`, `Zero Data Collection`, `Military-grade`.
- ZH-Keyword: **阅后即焚** (Fachbegriff für "burn after reading" — stärker als wörtliche Übersetzung).
- **Kritisch:** Kurzbeschreibung aktuell leer. Lange Beschreibung veraltet (erwähnt €1,79-Preismodell, kein IAP, keine Selbstzerstörung). Screenshots zeigen "13 Sprachen", App hat 15.

### Fertige Texte

#### en
- **Titel:** `snote.domezos: Self-Destruct`
- **Kurz:** `Encrypted notes & photos that self-destruct after 60 seconds. No account.`
- **Keywords:** self-destructing message, encrypted notes, Privnote alternative, burn after reading, secret note app, one-time secret
- **Lang:**
```
snote.domezos Secret Notes — send a message or photo that deletes itself after being read, in up to 60 seconds, then it's gone forever.

• E2E encrypted AES-256 — nobody but the recipient can read it, not even us
• Self-destructing: permanently destroyed after the timer
• Share photos, not just text — most Privnote-style tools don't support images
• No account — generate a link, send via WhatsApp/email/SMS
• Free, 15 languages

If you've used Privnote, One-Time Secret, or Burn Note — snote.domezos does the same plus image support.
```

#### de
- **Titel:** `snote.domezos: Selbstlöschend`
- **Kurz:** `Verschlüsselte Notizen & Fotos, die sich nach 60 Sekunden selbst zerstören.`
- **Keywords:** selbstzerstörende Nachricht, verschlüsselte Notiz, Privnote Alternative, selbstlöschende Nachricht, geheime Notiz, Passwort sicher teilen
- **Lang:**
```
snote.domezos Secret Notes – verschicke eine Nachricht oder ein Foto, das sich nach dem Lesen selbst löscht – spätestens nach 60 Sekunden, für immer.

• E2E-verschlüsselt AES-256 – nur der Empfänger kann lesen, nicht einmal wir
• Selbstzerstörend: Notiz/Bild nach Timer-Ablauf unwiderruflich gelöscht
• Auch Bilder teilen – die meisten Privnote-ähnlichen Tools können das nicht
• Kein Account – Link per WhatsApp/E-Mail/SMS senden
• Kostenlos, 15 Sprachen

Kennst du Privnote, One-Time Secret oder Burn Note? snote.domezos macht dasselbe – inklusive Bildversand.
```

#### es
- **Titel:** `snote.domezos: Nota Secreta`
- **Kurz:** `Notas y fotos cifradas que se autodestruyen en 60 segundos. Sin cuenta.`
- **Lang:**
```
• Cifrado E2E AES-256 — solo el destinatario puede leer, ni siquiera nosotros
• Autodestructiva: eliminada permanentemente al expirar el temporizador
• Comparte también fotos — la mayoría de herramientas tipo Privnote no lo permiten
• Sin cuenta — solo un enlace por WhatsApp/correo/SMS
• Gratis, 15 idiomas
```

#### fr
- **Titel:** `snote.domezos: Note Secrète`
- **Kurz:** `Notes et photos chiffrées qui s'autodétruisent après 60 secondes. Sans compte.`
- **Lang:**
```
• Chiffrement E2E AES-256 — seul le destinataire peut lire, même pas nous
• Autodestruction: supprimée définitivement à l'expiration du minuteur
• Partagez aussi des photos — la plupart des outils Privnote ne le permettent pas
• Sans compte — juste un lien par WhatsApp/e-mail/SMS
• Gratuit, 15 langues
```

#### it
- **Titel:** `snote.domezos: Autodistrutta`
- **Kurz:** `Note e foto cifrate che si autodistruggono dopo 60 secondi. Senza account.`
- **Lang:**
```
• Crittografia E2E AES-256 — solo il destinatario può leggere, nemmeno noi
• Autodistruzione: eliminata definitivamente allo scadere del timer
• Condividi anche foto — la maggior parte dei tool tipo Privnote non lo consente
• Nessun account — solo un link via WhatsApp/email/SMS
• Gratis, 15 lingue
```

#### pt
- **Titel:** `snote.domezos: Autodestrói`
- **Kurz:** `Notas e fotos criptografadas que se autodestroem após 60 segundos. Sem conta.`
- **Lang:**
```
• Criptografia E2E AES-256 — só o destinatário consegue ler, nem mesmo nós
• Autodestruição: apagada permanentemente ao fim do cronômetro
• Compartilhe também fotos — a maioria das ferramentas Privnote não permite
• Sem conta — só um link por WhatsApp/e-mail/SMS
• Grátis, 15 idiomas
```

#### ru
- **Titel:** `snote.domezos: Самоудаление`
- **Kurz:** `Зашифрованные заметки и фото, которые самоуничтожаются за 60 секунд.`
- **Lang:**
```
• Сквозное шифрование AES-256 — читает только получатель, даже мы не можем
• Самоуничтожение: удаляется безвозвратно по истечении таймера
• Можно делиться фото — большинство Privnote-инструментов этого не умеют
• Без аккаунта — просто ссылка через WhatsApp/email/SMS
• Бесплатно, 15 языков
```

#### ar
- **Titel:** `snote.domezos: تدمير ذاتي`
- **Kurz:** `ملاحظات وصور مشفرة تُدمَّر ذاتيًا خلال 60 ثانية. بدون حساب.`
- **Lang:**
```
• تشفير E2E بمعيار AES-256 — لا يقرأه سوى المستلم، ولا حتى نحن
• تدمير ذاتي: يُحذف نهائيًا عند انتهاء المؤقت
• شارك الصور أيضًا — معظم أدوات Privnote لا تدعم ذلك
• بدون حساب — رابط فقط عبر واتساب/بريد/رسائل
• مجانية، 15 لغة
```

#### hi
- **Titel:** `snote.domezos: स्व-नष्ट नोट`
- **Kurz:** `एन्क्रिप्टेड नोट्स और फ़ोटो जो 60 सेकंड में खुद नष्ट हो जाते हैं।`
- **Lang:**
```
• AES-256 E2E एन्क्रिप्शन — सिर्फ़ प्राप्तकर्ता पढ़ सकता है, हम भी नहीं
• स्व-नष्ट: टाइमर खत्म होते ही हमेशा के लिए डिलीट
• फ़ोटो भी शेयर करें — ज़्यादातर Privnote टूल्स में यह नहीं होता
• कोई अकाउंट नहीं — सिर्फ़ लिंक WhatsApp/ईमेल/SMS से
• मुफ़्त, 15 भाषाएं
```

#### bn
- **Titel:** `snote.domezos: স্ব-ধ্বংসী নোট`
- **Kurz:** `এনক্রিপ্ট করা নোট ও ছবি যা ৬০ সেকেন্ডে নিজে নিজে মুছে যায়।`
- **Lang:**
```
• AES-256 E2E এনক্রিপশন — শুধু প্রাপক পড়তে পারবে, আমরাও নয়
• স্ব-ধ্বংসী: টাইমার শেষে স্থায়ীভাবে মুছে যায়
• ছবিও শেয়ার করুন — বেশিরভাগ Privnote টুল এটি সাপোর্ট করে না
• কোনো অ্যাকাউন্ট নেই — শুধু লিঙ্ক WhatsApp/ইমেইল/SMS-এ
• ফ্রি, ১৫ ভাষা
```

#### ur
- **Titel:** `snote.domezos: خودکار تباہی`
- **Kurz:** `خفیہ نوٹس اور تصاویر جو 60 سیکنڈ میں خود بخود مٹ جاتی ہیں۔`
- **Lang:**
```
• AES-256 E2E انکرپشن — صرف وصول کنندہ پڑھ سکتا ہے، ہم بھی نہیں
• خودکار تباہی: ٹائمر ختم ہوتے ہی مستقل حذف
• تصاویر بھی شیئر کریں — زیادہ تر Privnote ٹولز یہ نہیں کر سکتے
• کوئی اکاؤنٹ نہیں — صرف لنک WhatsApp/ای میل/SMS سے
• مفت، 15 زبانیں
```

#### id
- **Titel:** `snote.domezos: Hancur Sendiri`
- **Kurz:** `Catatan & foto terenkripsi yang hancur sendiri dalam 60 detik. Tanpa akun.`
- **Lang:**
```
• Enkripsi E2E AES-256 — hanya penerima yang bisa membaca, kami pun tidak
• Hancur sendiri: dihapus permanen begitu timer habis
• Bisa berbagi foto — kebanyakan tool Privnote tidak mendukung ini
• Tanpa akun — cukup tautan via WhatsApp/email/SMS
• Gratis, 15 bahasa
```

#### ja
- **Titel:** `snote.domezos: 自己消滅メモ`
- **Kurz:** `60秒で自動的に消える暗号化メモ・写真。アカウント不要。`
- **Lang:**
```
• AES-256 E2Eエンドツーエンド暗号化 — 受信者のみ読めます
• 自己消滅：タイマー切れで完全削除
• テキストだけでなく写真も共有可能 — Privnote系ツールの多くは画像非対応
• アカウント不要 — リンクをWhatsApp/メール/SMSで送信するだけ
• 無料、15言語対応
```

#### ko
- **Titel:** `snote.domezos: 자동 삭제 메모`
- **Kurz:** `60초 후 스스로 사라지는 암호화된 메모와 사진. 계정 불필요.`
- **Lang:**
```
• AES-256 E2E 암호화 — 수신자 외에는 누구도 읽을 수 없음
• 자동 삭제: 타이머 종료 후 영구 삭제
• 텍스트뿐 아니라 사진도 공유 — Privnote 스타일 도구 대부분 이미지 미지원
• 계정 불필요 — 링크만 WhatsApp/이메일/SMS로 전송
• 무료, 15개 언어
```

#### zh-rCN
- **Titel:** `snote.domezos：阅后即焚笔记`
- **Kurz:** `60秒后自动销毁的加密笔记和照片，无需账号。`
- **Keywords:** 阅后即焚, 自毁消息, 加密笔记, Privnote 替代品, 安全发送密码
- **Lang:**
```
• AES-256 E2E加密 — 只有收件人能读取，连我们也无法看到
• 阅后即焚：计时结束后永久删除
• 不仅支持文字，还能分享照片 — 大多数Privnote类工具不支持图片
• 无需账号 — 只需生成链接，通过微信/邮件/短信发送
• 完全免费，支持15种语言
```

**Nächste Schritte:** Texte manuell in Play Console → Store-Präsenz → Haupt-Store-Eintrag eintragen (nicht im APK / `strings.xml`).

---

## 10. Kleines Testbudget (nach 1–9)

- **Kanal:** Reddit Ads, eng auf r/privacy-Interessenten gezielt.
- **Budget:** 20€ Obergrenze.
- **Ziel-Keywords:** "verschlüsselte Notiz App", "self-destructing message app", "Geheimnisse sicher teilen"
- **Vorgehen:** Kampagne auf Installs, nicht Klicks. Nach 20€ Cost-per-Install vs. Retention auswerten. Budget nur erhöhen nach Rückfrage + positivem Signal.

---

## Reihenfolge

Meiste Wirkung pro Aufwand: **7 → 9 → 8 → 1 → 6 → 2/3 → 4 → 5 → 10**
