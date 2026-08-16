<?php
declare(strict_types=1);

/**
 * API-Key-Prüfung + datei-basiertes Rate-Limiting für alle öffentlichen
 * Endpoints. Bewusst ohne DB/Redis (Standard-Shared-Hosting): Zähler sind
 * einfache Dateien pro Key+Zeitfenster, atomar über flock().
 *
 * Trennung Code/Daten: diese Datei ist öffentlich (Repo). Die echten Keys
 * liegen in data/keys.php (gitignored, nie committen). Vorlage: data/keys.example.php.
 */

// 'free' ist selbstbedienbar (Issue/PR). 'trusted'/'internal' sind kostenpflichtig — Preis wird
// per Kontaktaufnahme individuell vereinbart, siehe README.md "Public Backend API".
const RATE_TIERS = [
    'internal' => ['per_minute' => 120, 'per_day' => 50000],
    'trusted'  => ['per_minute' => 30,  'per_day' => 5000],
    'free'     => ['per_minute' => 10,  'per_day' => 1000],
];

function loadApiKeys(): array {
    $path = __DIR__ . '/data/keys.php';
    return is_file($path) ? require $path : [];
}

function apiFail(int $code, string $error): never {
    http_response_code($code);
    header('Content-Type: application/json');
    if ($code === 429) header('Retry-After: 60');
    echo json_encode(['error' => $error]);
    exit;
}

/** Reads the key from the X-API-Key header (preferred) or an apikey request param (WebView fallback). */
function providedApiKey(): string {
    $header = $_SERVER['HTTP_X_API_KEY'] ?? '';
    return $header !== '' ? $header : (string)($_REQUEST['apikey'] ?? '');
}

function checkRateWindow(string $dir, string $file, int $limit): void {
    if (!is_dir($dir)) mkdir($dir, 0700, true);
    $fp = fopen($dir . $file, 'c+');
    if (!$fp) return; // fail-open on filesystem issues, don't take the API down over a disk hiccup
    flock($fp, LOCK_EX);
    $count = (int)(stream_get_contents($fp) ?: '0');
    if ($count >= $limit) {
        flock($fp, LOCK_UN);
        fclose($fp);
        apiFail(429, 'rate_limited');
    }
    ftruncate($fp, 0);
    rewind($fp);
    fwrite($fp, (string)($count + 1));
    flock($fp, LOCK_UN);
    fclose($fp);
}

function cleanupStaleCounters(string $dir): void {
    if (random_int(1, 200) !== 1) return; // opportunistic, ~0.5% of requests
    $now = time();
    foreach (glob($dir . '*.cnt') ?: [] as $f) {
        if ($now - (filemtime($f) ?: 0) > 172800) @unlink($f);
    }
}

/** Call at the top of every public endpoint, right after requiring config.php. */
function enforceApiAccess(): string {
    $key = providedApiKey();
    $keys = loadApiKeys();

    if ($key === '' || !isset($keys[$key]) || ($keys[$key]['revoked'] ?? false)) {
        apiFail(401, 'invalid_or_missing_api_key');
    }

    $tier = $keys[$key]['tier'] ?? 'free';
    $limits = RATE_TIERS[$tier] ?? RATE_TIERS['free'];
    $dir = __DIR__ . '/data/ratelimit/';
    $safeKey = substr(hash('sha256', $key), 0, 16);

    checkRateWindow($dir, "{$safeKey}_min_" . date('YmdHi') . '.cnt', $limits['per_minute']);
    checkRateWindow($dir, "{$safeKey}_day_" . date('Ymd') . '.cnt', $limits['per_day']);
    cleanupStaleCounters($dir);

    return $key;
}
