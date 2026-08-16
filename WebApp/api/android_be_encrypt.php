<?php
declare(strict_types=1);
require_once __DIR__ . '/config.php';
require_once __DIR__ . '/auth.php';

date_default_timezone_set('Europe/Berlin');
$apiKey = enforceApiAccess();

$dir = __DIR__ . '/../msges/';
$days = 4;
$threshold = time() - ($days * 86400);
$exclude = '16.11.2025_18-03-49-200.txt';

foreach (glob($dir . '*.txt') ?: [] as $file) {
    $basename = basename($file);
    if ($basename !== $exclude && is_file($file) && filemtime($file) < $threshold) unlink($file);
}

$linksDir = __DIR__ . '/../links/';
$expireTime = time() - ($days * 86400);
foreach (glob($linksDir . '*.txt') ?: [] as $file) {
    if (filemtime($file) < $expireTime) unlink($file);
}

$Text = $_POST['write'] ?? $_GET['write'] ?? null;
// Optional image, sent as a plain (unencrypted) base64 JPEG string from the app.
// It is only ever encrypted client-side below, exactly like the text message.
$ImageB64 = $_POST['image'] ?? null;

http_response_code(200);
?>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>API Backend - snote</title>
</head>
<body>
    <div id="api-status">API is running...</div>

<script type="module">
const API_BASE = <?= json_encode(APP_BASE_URL) ?>;
const API_KEY = <?= json_encode($apiKey) ?>;

function pad(n) { return n.toString().padStart(2, '0'); }

function getTimestamp() {
    const now = new Date();
    const ms = now.getMilliseconds().toString().padStart(3, '0');
    return `${pad(now.getDate())}.${pad(now.getMonth() + 1)}.${now.getFullYear()}_${pad(now.getHours())}-${pad(now.getMinutes())}-${pad(now.getSeconds())}-${ms}`;
}

function safeDecodeURIComponent(str) {
    str = str.replace(/%(?![0-9A-Fa-f]{2})/g, '%25').replace(/%3E/gi, '>');
    try { return decodeURIComponent(str); } catch { return; }
}

function notifyAndroid(message) {
    if (window.Android?.notifyDataReady) {
        window.Android.notifyDataReady(JSON.stringify({ status: "ok", url: message }));
    }
}

function base64ToBytes(b64) {
    const binary = atob(b64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
    return bytes;
}

async function encryptAndSend(text, imageB64) {
    try {
        const pass = crypto.getRandomValues(new Uint8Array(16)).join('');
        const enc = new TextEncoder();
        const keyMaterial = await crypto.subtle.importKey("raw", enc.encode(pass), "PBKDF2", false, ["deriveKey"]);
        const key = await crypto.subtle.deriveKey(
            { name: "PBKDF2", salt: enc.encode("salt"), iterations: 100000, hash: "SHA-256" },
            keyMaterial, { name: "AES-GCM", length: 256 }, false, ["encrypt"]
        );
        const iv = crypto.getRandomValues(new Uint8Array(12));
        const encrypted = await crypto.subtle.encrypt({ name: "AES-GCM", iv }, key, enc.encode(text));
        const payload = { iv: Array.from(iv), data: Array.from(new Uint8Array(encrypted)) };

        if (imageB64 && imageB64 !== "null" && imageB64 !== "") {
            try {
                const imgIv = crypto.getRandomValues(new Uint8Array(12));
                const imgEncrypted = await crypto.subtle.encrypt({ name: "AES-GCM", iv: imgIv }, key, base64ToBytes(imageB64));
                payload.imgIv = Array.from(imgIv);
                payload.imgData = Array.from(new Uint8Array(imgEncrypted));
            } catch { /* image encrypt failure shouldn't block the text note */ }
        }

        const ts = getTimestamp();
        const res = await fetch(`${API_BASE}/api/msg_store.php?action=save&ts=${encodeURIComponent(ts)}`, {
            method: "POST",
            headers: { "Content-Type": "application/json", "X-API-Key": API_KEY },
            body: JSON.stringify(payload)
        });
        if (!res.ok) { notifyAndroid("Encrypt failed. Bad Internet Connection?"); return; }

        notifyAndroid(`${API_BASE}/msges/view.php?com=${encodeURIComponent(ts)}&pass=${encodeURIComponent(pass)}`);
    } catch {
        notifyAndroid("EncryptAndSend Failed");
    }
}

const text = <?= json_encode($Text) ?>;
const imageB64 = <?= json_encode($ImageB64) ?>;
if (text && text !== "null") {
    await encryptAndSend(safeDecodeURIComponent(text), imageB64);
}
</script>
</body>
</html>
