import { getSavedTheme, applyTheme } from "./theme.js";
import { getSavedLanguage, loadLocale, applyTranslations } from "./i18n.js";

async function init() {
  applyTheme(getSavedTheme());
  await loadLocale(getSavedLanguage());
  applyTranslations();
}

init();
