<?php
declare(strict_types=1);
require_once __DIR__ . '/config.php';

date_default_timezone_set('Europe/Berlin');
header('Content-Type: application/json; charset=utf-8');

const MSG_HONEYPOT_FILE = '16.11.2025_18-03-49-200.txt';
const MSG_HONEYPOT_PASS = '1671330142412131832322139820315246110200182';

function messagesDir(): string {
    return rtrim(MESSAGES_DIR, '/') . '/';
}

// Accepts either "TS" or "TS.txt" from the caller and always resolves to a
// safe "TS.txt" basename inside messagesDir() (no path traversal, no hidden files).
function resolveComName(string $raw): ?string {
    $name = basename(trim($raw));
    if ($name === '' || str_starts_with($name, '.')) return null;
    if (!str_ends_with($name, '.txt')) $name .= '.txt';
    return $name;
}

function msgSave(): void {
    $ts = $_GET['ts'] ?? '';
    $name = resolveComName((string)$ts);
    if ($name === null) {
        http_response_code(400);
        echo json_encode(['status' => 'error', 'error' => 'invalid_ts']);
        return;
    }

    $raw = file_get_contents('php://input');
    if ($raw === false || $raw === '') {
        http_response_code(400);
        echo json_encode(['status' => 'error', 'error' => 'empty_body']);
        return;
    }

    $decoded = json_decode($raw, true);
    if (!is_array($decoded) || !isset($decoded['iv'], $decoded['data'])) {
        http_response_code(400);
        echo json_encode(['status' => 'error', 'error' => 'invalid_payload']);
        return;
    }

    file_put_contents(messagesDir() . $name, $raw);
    echo json_encode(['status' => 'ok']);
}

function msgGet(): void {
    $com = (string)($_GET['com'] ?? '');
    $dir = messagesDir();
    $name = resolveComName($com);
    $path = $name !== null ? $dir . $name : null;

    if ($path !== null && file_exists($path)) {
        $data = file_get_contents($path);
        echo json_encode([
            'status' => 'ok',
            'pass_override' => null,
            'payload' => json_decode((string)$data),
        ]);
        return;
    }

    // Message not found or already expired: serve the honeypot payload with a
    // fixed key, exactly like the previous inline PHP behavior in view.php.
    $fallback = $dir . MSG_HONEYPOT_FILE;
    $data = file_exists($fallback) ? file_get_contents($fallback) : null;
    echo json_encode([
        'status' => 'not_found',
        'pass_override' => MSG_HONEYPOT_PASS,
        'payload' => $data !== null ? json_decode((string)$data) : null,
    ]);
}

function msgUnlink(): void {
    $com = (string)($_GET['com'] ?? '');
    $name = resolveComName($com);
    if ($name === null || $name === MSG_HONEYPOT_FILE) {
        echo json_encode(['status' => 'ignored']);
        return;
    }
    $path = messagesDir() . $name;
    if (file_exists($path)) {
        unlink($path);
    }
    echo json_encode(['status' => 'ok']);
}

$action = $_GET['action'] ?? '';
switch ($action) {
    case 'save':
        msgSave();
        break;
    case 'get':
        msgGet();
        break;
    case 'unlink':
        msgUnlink();
        break;
    default:
        http_response_code(400);
        echo json_encode(['status' => 'error', 'error' => 'unknown_action']);
}
