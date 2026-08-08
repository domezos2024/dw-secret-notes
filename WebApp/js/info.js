import { getSavedTheme, applyTheme } from "./theme.js";
import { getSavedLanguage, loadLocale, applyTranslations } from "./i18n.js";

// Detect current page extension and normalize all internal links
function detectPageExtension() {
  const pathname = window.location.pathname;
  return pathname.endsWith('.php') || pathname.includes('.php/') ? 'php' : 'html';
}

function normalizeInternalLinks() {
  const ext = detectPageExtension();
  const otherExt = ext === 'php' ? 'html' : 'php';
  
  // Replace all internal links with the correct extension
  document.querySelectorAll('a[href]').forEach(link => {
    const href = link.getAttribute('href');
    // Replace .html with .php or vice versa, but only for relative links
    if (href.includes(`index.${otherExt}`) || href.includes(`help.${otherExt}`) || href.includes(`info.${otherExt}`)) {
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
