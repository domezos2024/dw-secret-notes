# WebApp & WebApp_php Testing Guide

## Setup Overview

### Structure
- **WebApp/** — HTML version (uses `.html` extensions)
  - `index.html` - Main interface
  - `help.html` - Help page
  - `info.html` - Info page
  - `msges/view.html` - Decrypt link view
  - `css/`, `js/`, `i18n/` - Assets and modules

- **WebApp_php/** — PHP version (uses `.php` extensions)
  - `index.php` - Main interface
  - `help.php` - Help page
  - `info.php` - Info page
  - `msges/view.php` - Decrypt link view
  - `css/`, `js/`, `i18n/` - Assets and modules (identical to HTML version)

## Testing the HTML Version (Local File)

### Direct Browser Testing
1. Navigate to: `file:///d:/AndroidStudioProjects/dw-secret-notes/WebApp/index.html`
2. Select language and theme
3. Type a message or attach an image
4. Click "Generate secret link"
5. Copy the generated link (format: `file:///.../msges/view.html?com=...&pass=...`)
6. Click "Copy" or "Share"
7. Navigate to the copied link or paste in new tab
8. Message should decrypt and show with 60-second countdown
9. After 60 seconds, message auto-destroys

### Key Features Being Tested
✓ Encryption happens client-side (Web Crypto API - AES-256-GCM)
✓ Link generation with com (timestamp) and pass parameters
✓ All 15 languages load correctly
✓ All 17 themes apply correctly
✓ View page auto-decrypts from URL parameters
✓ 60-second countdown before auto-destruction
✓ Image display (base64 conversion)

## Testing the PHP Version (Local PHP Server)

### Setup PHP Server
```powershell
# Navigate to workspace root
cd d:\AndroidStudioProjects\dw-secret-notes

# Start PHP server on port 8000
php -S localhost:8000
```

### Browser Testing
1. Navigate to: `http://localhost:8000/WebApp_php/index.php`
2. Select language and theme
3. Type a message or attach an image
4. Click "Generate secret link"
5. Copy the generated link (format: `http://localhost:8000/WebApp_php/msges/view.php?com=...&pass=...`)
6. Click "Copy" or "Share"
7. Navigate to the copied link
8. Message should decrypt and show with 60-second countdown
9. After 60 seconds, message auto-destroys

### Identical Functionality
- Same encryption as HTML version (client-side)
- Same UI/UX and styling
- Same language and theme support
- Only difference: `.php` extensions for server compatibility

## Encryption/Decryption Flow

### Encryption (Local - Both Versions)
1. User enters text and/or image
2. `backend.js:encryptNote()` generates:
   - Random 16-byte password (as decimal string)
   - Random 12-byte IVs for each data piece
   - Derives AES-256-GCM key via PBKDF2(pass, "salt", 100k iterations, SHA-256)
3. Encrypts text and image with their own IVs
4. POSTs `{iv, data, imgIv?, imgData?}` to `https://domezos-ware.org/api/msg_store.php?action=save`
5. Backend returns timestamp as message ID (`com` parameter)
6. Link generated: `{RESULT_HOST}/msges/view.{ext}?com={timestamp}&pass={password}`

### Decryption (Local - Both Versions)
1. User navigates to view link with `com` and `pass` parameters
2. Page loads and calls `backend.js:decryptNote(com, pass, false)`
3. GETs message from `https://domezos-ware.org/api/msg_store.php?action=get&com={com}`
4. Client-side: derives same key from password + "salt" + PBKDF2
5. Decrypts message using AES-256-GCM with stored IV
6. Displays plaintext message
7. Starts 60-second countdown
8. Fires `msg_store.php?action=unlink` to self-destruct
9. After 60 seconds, message content hidden/destroyed

## Configuration

### Local Testing Mode (Automatic)
- When running from `localhost` or `127.0.0.1`, links point to local view pages
- When running from `file://` protocol, links point to local HTML files
- HTML version uses `.html`, PHP version uses `.php` automatically

### Production Mode (Automatic)
- When running from any other host, links point to `https://domezos-ware.org/msges/view.php`
- Backend storage always uses live server (no local database)

## Testing Checklist

### HTML Version (WebApp)
- [ ] Encrypt text → generates link
- [ ] Navigate link → message decrypts
- [ ] Image attach → encrypts and decrypts
- [ ] Countdown works (60 seconds)
- [ ] Message disappears after countdown
- [ ] Language selector works (all 15 languages)
- [ ] Theme selector works (all 17 themes)
- [ ] Help and Info pages load
- [ ] Link copy/share works

### PHP Version (WebApp_php)
- [ ] All above tests
- [ ] Works with PHP server (localhost:8000)
- [ ] .php extensions work correctly

### Cross-Version
- [ ] HTML version links work in PHP server environment (if needed)
- [ ] PHP version links work in HTML file environment (if needed)

## Troubleshooting

### CORS Issues
- Local file:// may block cross-origin requests to domezos-ware.org
- Solution: Use PHP server or configure browser CORS locally

### Links Not Working
- Check browser console for errors (F12)
- Verify message storage endpoint reachable: `https://domezos-ware.org/api/msg_store.php`
- Verify `com` and `pass` parameters in URL are correct

### Language/Theme Not Loading
- Check `i18n/` and `css/` directories exist
- Verify file paths in HTML (relative paths should work)
- Check browser console for 404 errors

### PHP Errors
- Ensure PHP 7.2+ installed
- PHP server mode doesn't need .php execution (just serves files)
- For actual PHP logic, use separate PHP server instance

## Notes

- **Crypto is 100% client-side** — no backend processing of plaintext
- **No local database** — messages stored on domezos-ware.org
- **Self-destructing** — messages deleted after first read
- **Languages & Themes** — Fully supported in both versions
- **PBKDF2 iterations** — 100,000 (industry standard for key derivation)
- **Encryption** — AES-256-GCM with random IVs
