<?php
declare(strict_types=1);

define('APP_HOST', 'domezos-ware.com');
define('APP_BASE_URL', 'https://' . APP_HOST);

define('DB_HOST', 'database-5019813354.webspace-host.com');
define('DB_NAME', 'dbs15339207');
define('DB_USER', 'dbu701371');
define('DB_PASS', 'Real.Fuck69');
define('DB_CHARSET', 'utf8mb4');

// Absoluter Serverpfad zum msges/ Verzeichnis
define('MESSAGES_DIR', dirname(__DIR__) . '/msges/');

// Absoluter Serverpfad zum TinyURL-Kurzlink-Verzeichnis (anpassen!)
define('LINKS_DIR', dirname(__DIR__) . '/links/');

// API Secret — muss mit dem Netlify Env Var API_SECRET übereinstimmen
define('API_SECRET', '09c259968db1206b562eed64d2966eaf4f260f3f828d50cbf60b200eb8fcb4dc');
