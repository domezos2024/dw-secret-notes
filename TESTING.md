# WebApp & WebApp_php Testing Guide

## Setup

- **WebApp/** — HTML version (`index.html`, `help.html`, `info.html`, `msges/view.html`)
- **WebApp_php/** — PHP version (identical structure, `.php` extensions)

Both share the same `css/`, `js/`, `i18n/` assets.

## HTML Version (local file)

Open `file:///d:/AndroidStudioProjects/dw-secret-notes/WebApp/index.html` in browser.

1. Select language + theme.
2. Enter text or attach image → click **Generate secret link**.
3. Copy link (`file:///.../msges/view.html?com=...&pass=...`).
4. Navigate to link → message decrypts with 60-second countdown → auto-destroys.

## PHP Version (local server)

```powershell
cd d:\AndroidStudioProjects\dw-secret-notes
php -S localhost:8000
```

Open `http://localhost:8000/WebApp_php/index.php`. Same flow as above; links use `.php` extensions.

## Encryption / Decryption Flow

**Encrypt:**
1. `backend.js:encryptNote()` — random 16-byte password, random 12-byte IVs, PBKDF2(pass, "salt", 100k, SHA-256) → AES-256-GCM key.
2. Encrypts text + image (separate IVs).
3. POSTs `{iv, data, imgIv?, imgData?}` to `https://domezos-ware.com/api/msg_store.php?action=save`.
4. Backend returns timestamp (`com`). Link: `{RESULT_HOST}/msges/view.{ext}?com={ts}&pass={pw}`.

**Decrypt:**
1. Page loads, calls `backend.js:decryptNote(com, pass, false)`.
2. GETs ciphertext from `msg_store.php?action=get&com={com}`.
3. Client derives key, decrypts, displays message.
4. Starts 60-second countdown, fires `msg_store.php?action=unlink`.

**Local vs. production:** On `localhost`/`file://`, links point to local view pages. On any other host, links always point to `https://domezos-ware.com/msges/view.php`.

## Checklists

**HTML (WebApp):**
- [ ] Encrypt text → link generated
- [ ] Navigate link → message decrypts
- [ ] Image attach → encrypts + decrypts
- [ ] 60-second countdown + auto-destroy
- [ ] Language selector (15 languages)
- [ ] Theme selector (17 themes)
- [ ] Help + Info pages load
- [ ] Copy/Share works

**PHP (WebApp_php):**
- [ ] All above
- [ ] PHP server works on localhost:8000
- [ ] `.php` extensions correct

**Cross-version:**
- [ ] HTML link decryptable in PHP environment
- [ ] PHP link decryptable in HTML environment

## Troubleshooting

| Issue | Fix |
|---|---|
| CORS error (file://) | Use PHP server or configure browser CORS |
| Link not working | Check console (F12); verify `com`+`pass` params; check `msg_store.php` reachable |
| Language/theme missing | Check `i18n/`+`css/` paths; clear cache |
| PHP error | PHP 7.2+ required |

**Notes:** Crypto is 100% client-side. No local DB — messages stored on domezos-ware.com. PBKDF2 iterations: 100k.
