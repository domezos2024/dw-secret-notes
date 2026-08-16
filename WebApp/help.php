<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>dw Secret Notes — Help</title>
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
        <a class="icon-btn" href="info.php" data-i18n="nav_info">Info</a>
      </nav>
    </header>

    <div class="card">
      <h1 data-i18n="help_title">Help &amp; FAQ</h1>

      <h2 data-i18n="help_how_title">How it works</h2>
      <p data-i18n="help_how_body"></p>

      <h2 data-i18n="help_security_title">Highest Security (AES 256-bit)</h2>
      <p data-i18n="help_security_body"></p>
    </div>

    <div class="card">
      <div class="faq-item">
        <p class="faq-q" data-i18n="help_faq_q1"></p>
        <p class="faq-a" data-i18n="help_faq_a1"></p>
      </div>
      <div class="faq-item">
        <p class="faq-q" data-i18n="help_faq_q2"></p>
        <p class="faq-a" data-i18n="help_faq_a2"></p>
      </div>
      <div class="faq-item">
        <p class="faq-q" data-i18n="help_faq_q3"></p>
        <p class="faq-a" data-i18n="help_faq_a3"></p>
      </div>
      <div class="faq-item">
        <p class="faq-q" data-i18n="help_faq_q4"></p>
        <p class="faq-a" data-i18n="help_faq_a4"></p>
      </div>
    </div>

    <footer class="app-footer">
      <div><a href="https://domezos-ware.com" data-i18n="footer_website">https://domezos-ware.com</a></div>
      <div data-i18n="footer_copyright">© 2026 DoMeZos-Ware</div>
    </footer>
  </div>

  <script type="module" src="js/help.js"></script>
  <script>
    if ("serviceWorker" in navigator) {
      navigator.serviceWorker.register("/sw.js");
    }
  </script>
</body>
</html>
