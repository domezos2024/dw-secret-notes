# WebApp & WebApp_php Compatibility Guide

Both **WebApp/** (HTML) and **WebApp_php/** (PHP) are fully compatible and interchangeable. Messages encrypted in one version can be decrypted in the other.

## File Structure

```
WebApp/                          WebApp_php/
├── index.html / index.php       ├── index.php / index.html (fallback)
├── help.html / help.php         ├── help.php / help.html
├── info.html / info.php         ├── info.php / info.html
├── msges/view.html + view.php   ├── msges/view.php + view.html
├── css/ (base.css, themes.css)  ├── css/ (identical)
├── js/ (see below)              ├── js/ (identical)
└── i18n/ (15 languages)        └── i18n/ (identical)
```

**JS modules:** `main.js`, `help.js`, `info.js`, `backend.js`, `i18n.js`, `theme.js`, `aliasParser.js`, `imageUtils.js`

## Smart Extension Detection

All JS modules auto-detect `.html` vs `.php` and adjust links accordingly:

```javascript
function detectPageExtension() {
  const pathname = window.location.pathname;
  return pathname.endsWith('.php') || pathname.includes('.php/') ? 'php' : 'html';
}
```

`backend.js` link generation:
- **Local** (`localhost`, `127.0.0.1`, `file://`) → uses local paths with detected extension.
- **Live** (`domezos-ware.com`) → always generates `https://domezos-ware.com/msges/view.php`.

## Crypto Compatibility

All versions use identical client-side encryption:
- AES-256-GCM (Web Crypto API)
- PBKDF2 — 100k iterations, `salt="salt"`, SHA-256
- Same backend: `https://domezos-ware.com/api/msg_store.php`

## Deployment Options

| Option | Deploy | Links |
|---|---|---|
| A — HTML primary | `WebApp/` → `/webapp/` | `.html` by default, `.php` for server compat |
| B — PHP primary | `WebApp_php/` → `/webapp/` | `.php` by default, `.html` as fallback |
| C — Both | Both with URL routing | Each user picks preferred URL; messages are cross-compatible |

No config files or environment variables needed — auto-detection is fully automatic. `localStorage` keys (`language`, `theme`) are shared across versions on the same origin.

## Troubleshooting

| Issue | Fix |
|---|---|
| Links not normalizing | Check browser console (F12) for JS errors; clear cache |
| Different encryption results | Should not happen — check `PBKDF2_SALT`/`PBKDF2_ITERATIONS` in `backend.js` |
| Lost language/theme | `file://` and `http://localhost` have different origins; localStorage is per-origin |
| CORS errors (local file://) | Use PHP server or configure browser CORS |
| PHP errors | PHP 7.2+ required |
