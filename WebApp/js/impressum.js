import { getSavedTheme, applyTheme } from "./theme.js";
import { getSavedLanguage, loadLocale, applyTranslations } from "./i18n.js";

function detectPageExtension() {
  const pathname = window.location.pathname;
  return pathname.endsWith('.php') || pathname.includes('.php/') ? 'php' : 'html';
}

function normalizeInternalLinks() {
  const ext = detectPageExtension();
  const otherExt = ext === 'php' ? 'html' : 'php';
  
  document.querySelectorAll('a[href]').forEach(link => {
    const href = link.getAttribute('href');
    if (href.includes(`index.${otherExt}`) || href.includes(`help.${otherExt}`) || href.includes(`info.${otherExt}`) || href.includes(`impressum.${otherExt}`)) {
      const newHref = href.replace(`.${otherExt}`, `.${ext}`);
      link.setAttribute('href', newHref);
    }
  });
}

async function init() {
  normalizeInternalLinks();
  applyTheme(getSavedTheme());
  await loadLocale(getSavedLanguage());
  applyTranslations();
}

init();
