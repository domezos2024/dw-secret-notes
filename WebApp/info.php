<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>dw Secret Notes — Info</title>
  <link rel="stylesheet" href="css/themes.css" />
  <link rel="stylesheet" href="css/base.css" />
  <link rel="manifest" href="manifest.webapp.json" />
  <meta name="theme-color" content="#050D1F" />
</head>
<body>
  <div class="app-shell">
    <header class="app-header">
      <a href="index.php" class="brand">
        <span class="dot"></span>
        <span data-i18n="app_name">dw Secret Notes</span>
      </a>
      <nav class="app-nav">
        <a class="icon-btn" href="index.php" data-i18n="cd_back">Back</a>
        <a class="icon-btn" href="help.php" data-i18n="nav_help">Help</a>
      </nav>
    </header>

    <div class="card">
      <h1 data-i18n="info_title">About</h1>
      <p data-i18n="app_name">dw Secret Notes</p>
      <p data-i18n="info_crypto_note"></p>
    </div>

    <div class="card">
      <p class="status-text" data-i18n="info_web_note"></p>
    </div>

    <footer class="app-footer">
      <div><a href="https://domezos-ware.com" data-i18n="footer_website">https://domezos-ware.com</a></div>
      <div data-i18n="footer_copyright">© 2026 DoMeZos-Ware</div>
    </footer>
  </div>

  <script type="module" src="js/info.js"></script>
  <script>
    if ("serviceWorker" in navigator) {
      navigator.serviceWorker.register("/sw.js");
    }
  </script>
</body>
</html>
