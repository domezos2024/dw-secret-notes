<?php
// Vorlage für api/data/keys.php — diese Datei selbst wird NICHT ausgewertet
// (keys.php mit den echten Keys ist gitignored, siehe .gitignore).
//
// Key erzeugen: php -r "echo bin2hex(random_bytes(24));"
// Tiers und Limits: siehe RATE_TIERS in auth.php.
return [
    'REPLACE_WITH_REAL_KEY' => ['owner' => 'internal-app', 'tier' => 'internal', 'revoked' => false],
    'REPLACE_WITH_ANOTHER_KEY' => ['owner' => 'github_u/example', 'tier' => 'free', 'revoked' => false],
];
