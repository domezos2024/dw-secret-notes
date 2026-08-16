<?php
declare(strict_types=1);
require_once __DIR__ . '/config.php';

header('Content-Type: application/json');

if (($_SERVER['HTTP_X_API_KEY'] ?? '') !== API_SECRET) {
    http_response_code(401);
    echo json_encode(['error' => 'Unauthorized']);
    exit;
}

$module = $_GET['module'] ?? '';
$action = $_GET['action'] ?? '';

try {
    echo match ($module) {
        'devices'  => handleDevices($action),
        'messages' => handleMessages($action),
        'links'    => handleLinks($action),
        default    => throw new InvalidArgumentException('Unknown module: ' . $module),
    };
} catch (Throwable $e) {
    http_response_code(400);
    echo json_encode(['error' => $e->getMessage()]);
}

function db(): PDO {
    static $pdo = null;
    if ($pdo === null) {
        $dsn = sprintf('mysql:host=%s;dbname=%s;charset=%s', DB_HOST, DB_NAME, DB_CHARSET);
        $pdo = new PDO($dsn, DB_USER, DB_PASS, [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        ]);
    }
    return $pdo;
}

function handleDevices(string $action): string {
    return match ($action) {
        'list'   => devList(),
        'get'    => devGet(),
        'search' => devSearch(),
        default  => throw new InvalidArgumentException('Unknown action: ' . $action),
    };
}

function devList(): string {
    $limit = min((int)($_GET['limit'] ?? 50), 200);
    $where = '';
    $params = [];
    if (isset($_GET['premium'])) {
        $where = 'WHERE premium = :premium';
        $params[':premium'] = (int)$_GET['premium'];
    }
    $stmt = db()->prepare("SELECT * FROM devices $where ORDER BY id DESC LIMIT :limit");
    foreach ($params as $k => $v) $stmt->bindValue($k, $v, PDO::PARAM_INT);
    $stmt->bindValue(':limit', $limit, PDO::PARAM_INT);
    $stmt->execute();
    $rows = $stmt->fetchAll();
    return empty($rows) ? 'Keine Geräte gefunden.' : json_encode($rows, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
}

function devGet(): string {
    $stmt = db()->prepare('SELECT * FROM devices WHERE id = :id LIMIT 1');
    $stmt->execute([':id' => (int)($_GET['id'] ?? 0)]);
    $row = $stmt->fetch();
    return $row ? json_encode($row, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) : 'Gerät nicht gefunden.';
}

function devSearch(): string {
    $q = '%' . ($_GET['query'] ?? '') . '%';
    $stmt = db()->prepare('SELECT * FROM devices WHERE token LIKE :q OR Bemerkungen LIKE :q2 ORDER BY id DESC LIMIT 50');
    $stmt->execute([':q' => $q, ':q2' => $q]);
    $rows = $stmt->fetchAll();
    return empty($rows) ? 'Keine Treffer.' : json_encode($rows, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
}

function handleMessages(string $action): string {
    $dir = rtrim(MESSAGES_DIR, '/') . '/';
    if (!is_dir($dir)) return json_encode(['error' => 'Verzeichnis nicht gefunden: ' . $dir]);
    return match ($action) {
        'count'  => count(glob($dir . '*.txt') ?: []) . ' Nachricht(en).',
        'list'   => (function() use ($dir) {
            $files = array_map('basename', glob($dir . '*.txt') ?: []);
            return empty($files) ? 'Keine Nachrichten.' : implode("\n", $files);
        })(),
        'read'   => msgRead($dir),
        'delete' => msgDelete($dir),
        default  => throw new InvalidArgumentException('Unknown action: ' . $action),
    };
}

function msgRead(string $dir): string {
    $f = basename($_GET['filename'] ?? '');
    if (!str_ends_with($f, '.txt')) return 'Nur .txt erlaubt.';
    $path = $dir . $f;
    if (!file_exists($path)) return 'Nicht gefunden: ' . $f;
    return file_get_contents($path) ?: 'Lesefehler.';
}

function msgDelete(string $dir): string {
    $f = basename($_GET['filename'] ?? '');
    if (!str_ends_with($f, '.txt')) return 'Nur .txt erlaubt.';
    $path = $dir . $f;
    if (!file_exists($path)) return 'Nicht gefunden: ' . $f;
    return unlink($path) ? 'Gelöscht: ' . $f : 'Löschen fehlgeschlagen.';
}

function handleLinks(string $action): string {
    $dir = rtrim(LINKS_DIR, '/') . '/';
    if (!is_dir($dir)) return json_encode(['error' => 'Verzeichnis nicht gefunden: ' . $dir]);
    $folders = array_values(array_filter(scandir($dir) ?: [], fn($i) => !str_starts_with($i, '.') && is_dir($dir . $i)));
    return match ($action) {
        'count'  => count($folders) . ' aktive Link(s).',
        'list'   => empty($folders) ? 'Keine Links.' : implode("\n", $folders),
        'delete' => linksDelete($dir),
        default  => throw new InvalidArgumentException('Unknown action: ' . $action),
    };
}

function linksDelete(string $dir): string {
    $folder = basename($_GET['folder'] ?? '');
    if ($folder === '' || str_starts_with($folder, '.')) return 'Ungültiger Ordnername.';
    $path = $dir . $folder;
    if (!is_dir($path)) return 'Nicht gefunden: ' . $folder;
    return removeDir($path) ? 'Gelöscht: ' . $folder : 'Löschen fehlgeschlagen.';
}

function removeDir(string $path): bool {
    foreach (scandir($path) ?: [] as $item) {
        if ($item === '.' || $item === '..') continue;
        $full = $path . '/' . $item;
        is_dir($full) ? removeDir($full) : unlink($full);
    }
    return rmdir($path);
}
