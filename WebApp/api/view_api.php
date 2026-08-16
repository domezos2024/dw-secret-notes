<?php
declare(strict_types=1);
require_once __DIR__ . '/config.php';
require_once __DIR__ . '/auth.php';

date_default_timezone_set('Europe/Berlin');
$apiKey = enforceApiAccess();

$com = $_GET['com'] ?? '';
$pass = $_GET['pass'] ?? '';
?>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8" /></head>
<body>
    <!-- Minimales Interface für die App -->
    <script type="module">
        const API_BASE = <?= json_encode(APP_BASE_URL) ?>;
        const API_KEY = <?= json_encode($apiKey) ?>;
        const com = <?= json_encode($com) ?>;
        const urlPass = decodeURIComponent("<?= $pass ?>");

        function bytesToBase64(bytes) {
            let binary = '';
            const chunkSize = 0x8000;
            for (let i = 0; i < bytes.length; i += chunkSize) {
                binary += String.fromCharCode.apply(null, bytes.subarray(i, i + chunkSize));
            }
            return btoa(binary);
        }

        // Gleiche Crypto-Logik wie in msges/view.php, Daten kommen aus api/msg_store.php
        async function decrypt() {
            try {
                const res = await fetch(`${API_BASE}/api/msg_store.php?action=get&com=${encodeURIComponent(com)}`, {
                    headers: { "X-API-Key": API_KEY }
                });
                const json = await res.json();
                const payload = json.payload;
                const pass = json.pass_override ?? urlPass;
                if (!payload) throw new Error("no payload");

                const enc = new TextEncoder();
                const dec = new TextDecoder();
                const keyMaterial = await crypto.subtle.importKey("raw", enc.encode(pass), "PBKDF2", false, ["deriveKey"]);
                const key = await crypto.subtle.deriveKey(
                    { name: "PBKDF2", salt: enc.encode("salt"), iterations: 100000, hash: "SHA-256" },
                    keyMaterial, { name: "AES-GCM", length: 256 }, false, ["decrypt"]
                );

                const decrypted = await crypto.subtle.decrypt(
                    { name: "AES-GCM", iv: new Uint8Array(payload.iv) },
                    key, new Uint8Array(payload.data)
                );
                const result = dec.decode(decrypted);

                // Daten an die Android App übergeben
                if (window.Android && window.Android.sendAnswer) {
                    window.Android.sendAnswer(result);
                }

                if (payload.imgIv && payload.imgData) {
                    try {
                        const imgDecrypted = await crypto.subtle.decrypt(
                            { name: "AES-GCM", iv: new Uint8Array(payload.imgIv) },
                            key, new Uint8Array(payload.imgData)
                        );
                        if (window.Android && window.Android.sendImage) {
                            window.Android.sendImage(bytesToBase64(new Uint8Array(imgDecrypted)));
                        }
                    } catch (imgErr) {
                        console.error("Image decrypt error:", imgErr);
                    }
                }

                // Unlink Aufruf
                fetch(`${API_BASE}/api/msg_store.php?action=unlink&com=${encodeURIComponent(com)}`, {
                    headers: { "X-API-Key": API_KEY }
                });
            } catch (err) {
                if (window.Android && window.Android.sendAnswer) {
                    window.Android.sendAnswer("ERROR: Decryption failed");
                }
            }
        }
        decrypt();
    </script>
</body>
</html>
