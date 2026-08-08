# WebApp & WebApp_php Full Compatibility Guide

## Overview

Both **WebApp/** (HTML version) and **WebApp_php/** (PHP version) are **fully compatible** and can work in identical environments. This enables seamless deployment scenarios where both versions exist simultaneously.

## File Structure & Compatibility

### WebApp Directory (HTML Primary)
```
WebApp/
├── index.html ............ Main page (HTML)
├── index.php ............ Main page (PHP alias)
├── help.html ............ Help page (HTML)
├── help.php ............ Help page (PHP alias)
├── info.html ............ Info page (HTML)
├── info.php ............ Info page (PHP alias)
├── msges/
│   ├── view.html ........ Decrypt page (HTML)
│   └── view.php ........ Decrypt page (PHP alias)
├── css/
│   ├── base.css ........ Styling
│   └── themes.css ...... 17 theme definitions
├── js/
│   ├── main.js ......... Encryption/UI logic (auto-detects extensions)
│   ├── help.js ......... Help page logic (auto-detects extensions)
│   ├── info.js ......... Info page logic (auto-detects extensions)
│   ├── backend.js ...... Crypto & link generation (smart detection)
│   ├── i18n.js ......... 15 language support
│   ├── theme.js ........ Theme management
│   ├── aliasParser.js .. Link parsing
│   └── imageUtils.js ... Image processing
└── i18n/
    └── 15 language files (ar, bn, de, en, es, fr, hi, id, it, ja, ko, pt, ru, ur, zh-CN)
```

### WebApp_php Directory (PHP Primary)
```
WebApp_php/
├── index.php ............ Main page (PHP)
├── help.php ............ Help page (PHP)
├── info.php ............ Info page (PHP)
├── msges/
│   ├── view.php ........ Decrypt page (PHP)
│   └── view.html ....... Decrypt page (HTML fallback)
├── css/ ................ Identical to WebApp
├── js/ ................. Identical to WebApp (same auto-detection logic)
└── i18n/ ............... Identical to WebApp
```

## Smart Extension Detection

### How It Works

All JavaScript modules detect whether they're running from `.html` or `.php` and automatically adjust behavior:

```javascript
function detectPageExtension() {
  const pathname = window.location.pathname;
  return pathname.endsWith('.php') || pathname.includes('.php/') ? 'php' : 'html';
}

function normalizeInternalLinks() {
  // On page load, converts all links to the correct extension
  // Example: "help.html" → "help.php" (if running from .php)
}
```

### Affected Modules

| Module | Detection | Impact |
|--------|-----------|--------|
| `main.js` | Page extension | Link normalization |
| `help.js` | Page extension | Link normalization |
| `info.js` | Page extension | Link normalization |
| `backend.js` | Host + pathname | Link generation |

### Link Generation Logic (backend.js)

```javascript
function getResultLongHost() {
  // 1. Local testing (localhost, 127.0.0.1, file://)
  //    → Uses local paths with detected extension
  //    → HTML generates: /WebApp/msges/view.html
  //    → PHP generates: /WebApp_php/msges/view.php
  
  // 2. Live server (domezos-ware.org)
  //    → Always generates: https://domezos-ware.org/msges/view.php
}
```

## Testing Scenarios

### Scenario 1: Access WebApp/index.html
```
Browser: file:///d:/AndroidStudioProjects/dw-secret-notes/WebApp/index.html
Links:   index.html, help.html, info.html
Encrypt: Generates link with com=...&pass=... 
View:    file:///.../msges/view.html
```

### Scenario 2: Access WebApp/index.php (PHP server)
```
Browser: http://localhost:8000/WebApp/index.php
Links:   Normalized to index.php, help.php, info.php
Encrypt: Generates link http://localhost:8000/WebApp/msges/view.php
View:    http://localhost:8000/WebApp/msges/view.php
```

### Scenario 3: Access WebApp_php/index.php (PHP server)
```
Browser: http://localhost:8000/WebApp_php/index.php
Links:   index.php, help.php, info.php (already correct)
Encrypt: Generates link http://localhost:8000/WebApp_php/msges/view.php
View:    http://localhost:8000/WebApp_php/msges/view.php
```

### Scenario 4: Live Server (Mixed content)
```
Server:  https://domezos-ware.org/webapp/
Files:   index.html, index.php, help.html, help.php, info.html, info.php,
         msges/view.html, msges/view.php

User accesses: https://domezos-ware.org/webapp/index.html
Links:   Normalized to index.html, help.html, info.html
Encrypt: Generates link https://domezos-ware.org/webapp/msges/view.html

User accesses: https://domezos-ware.org/webapp/index.php  
Links:   Normalized to index.php, help.php, info.php
Encrypt: Generates link https://domezos-ware.org/msges/view.php
```

## Crypto Compatibility

**IMPORTANT**: All versions use **identical client-side encryption**:
- AES-256-GCM encryption (Web Crypto API)
- PBKDF2 key derivation (100,000 iterations)
- Same backend storage: `https://domezos-ware.org/api/msg_store.php`
- Messages encrypted in WebApp can be decrypted in WebApp_php (and vice versa)

## Deployment on Live Server

### Option A: HTML Primary
1. Deploy `WebApp/` contents to `/webapp/`
2. All links use `.html` by default
3. `.php` files included for PHP server compatibility

### Option B: PHP Primary  
1. Deploy `WebApp_php/` contents to `/webapp/`
2. All links use `.php` by default
3. `.html` files included for fallback compatibility

### Option C: Both (Dual Mode)
1. Deploy both directories with URL routing:
   - `/webapp/index.html` → WebApp version
   - `/webapp/index.php` → WebApp_php version
2. Both can coexist and share encrypted messages
3. Each user picks their preferred URL

## Configuration Files

### No Configuration Needed
- Auto-detection is fully automatic
- No config files or environment variables required
- Works identically whether running from HTML or PHP

### localStorage
- `language` — User's language preference (synced across versions)
- `theme` — User's theme preference (synced across versions)

## Cross-Version Compatibility Test

### Create Message in WebApp
```
1. Open: file:///WebApp/index.html
2. Encrypt: "Hello from HTML"
3. Copy link: file:///.../msges/view.html?com=...&pass=...
4. Change extension: file:///.../msges/view.php?com=...&pass=...
5. Navigate → Message decrypts ✓
```

### Create Message in WebApp_php
```
1. Open: http://localhost:8000/WebApp_php/index.php
2. Encrypt: "Hello from PHP"
3. Copy link: http://localhost:8000/WebApp_php/msges/view.php?com=...&pass=...
4. Change to HTML: http://localhost:8000/WebApp_php/msges/view.html?com=...&pass=...
5. Navigate → Message decrypts ✓
```

## Troubleshooting

### Links Not Auto-Normalizing
- Check browser console for JS errors (F12)
- Verify JavaScript modules load correctly
- Clear browser cache and reload

### Different Encryption Results  
- Should NOT happen — crypto is identical
- Check PBKDF2_SALT, PBKDF2_ITERATIONS in backend.js
- Verify Web Crypto API version compatibility

### Lost Language/Theme Preference
- localStorage preferences are per-domain
- `file://` and `http://localhost` have different origins
- Create explicit localStorage sync if needed

## Summary

✅ **Both versions are 100% compatible**
✅ **Auto-detection handles all scenarios**
✅ **Messages encrypt/decrypt across versions**
✅ **No configuration required**
✅ **Works locally, on PHP server, and production**
✅ **Same security, same crypto, same UX**

Deploy with confidence — pick the version that fits your infrastructure!
