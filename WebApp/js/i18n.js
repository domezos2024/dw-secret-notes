/**
 * Locale loading/persistence/interpolation. Language list & order mirrors
 * LANGUAGES in LanguageScreen.kt. Plural handling is simplified to a
 * one/other pair per language (see WebApp plan) rather than full ICU;
 * Arabic/Urdu additionally flip the page to RTL, matching what Compose
 * does automatically for those locales on Android.
 */

export const LANGUAGES = [
  { tag: "en", native: "English", english: "English" },
  { tag: "de", native: "Deutsch", english: "German" },
  { tag: "es", native: "Español", english: "Spanish" },
  { tag: "zh-CN", native: "中文", english: "Chinese (Simplified)" },
  { tag: "hi", native: "हिन्दी", english: "Hindi" },
  { tag: "ar", native: "العربية", english: "Arabic" },
  { tag: "pt", native: "Português", english: "Portuguese" },
  { tag: "bn", native: "বাংলা", english: "Bengali" },
  { tag: "ru", native: "Русский", english: "Russian" },
  { tag: "ja", native: "日本語", english: "Japanese" },
  { tag: "fr", native: "Français", english: "French" },
  { tag: "ur", native: "اردو", english: "Urdu" },
  { tag: "id", native: "Indonesia", english: "Indonesian" },
  { tag: "ko", native: "한국어", english: "Korean" },
  { tag: "it", native: "Italiano", english: "Italian" },
];

const RTL_LANGS = new Set(["ar", "ur"]);
const STORAGE_KEY = "dw_lang";
const DEFAULT_LANG = "en";

let enStrings = null;
let currentStrings = {};
let currentLang = DEFAULT_LANG;

async function fetchLocale(tag) {
  // Resolve relative to this module's own location, not the page's URL, so
  // it works the same from pages at any depth (e.g. msges/view.html).
  const res = await fetch(new URL(`../i18n/${tag}.json`, import.meta.url));
  if (!res.ok) throw new Error(`Locale not found: ${tag}`);
  return res.json();
}

export function getSavedLanguage() {
  const saved = localStorage.getItem(STORAGE_KEY);
  if (saved && LANGUAGES.some((l) => l.tag === saved)) return saved;
  return DEFAULT_LANG;
}

export function setSavedLanguage(tag) {
  localStorage.setItem(STORAGE_KEY, tag);
}

export async function loadLocale(tag) {
  if (!enStrings) {
    enStrings = await fetchLocale(DEFAULT_LANG);
  }
  if (tag === DEFAULT_LANG) {
    currentStrings = enStrings;
  } else {
    try {
      currentStrings = await fetchLocale(tag);
    } catch {
      console.warn(`[i18n] Falling back to English, missing locale: ${tag}`);
      currentStrings = enStrings;
      tag = DEFAULT_LANG;
    }
  }
  currentLang = tag;
  applyDirection(tag);
  return currentStrings;
}

function format(template, args) {
  let i = 0;
  return template.replace(/%[ds]/g, () => (args[i++] ?? ""));
}

export function t(key, ...args) {
  const template = currentStrings[key] ?? enStrings?.[key] ?? key;
  return args.length ? format(template, args) : template;
}

/** Simplified plural: picks `${baseKey}_one` for count===1, else `${baseKey}_other`. */
export function tPlural(baseKey, count) {
  const suffix = count === 1 ? "one" : "other";
  return t(`${baseKey}_${suffix}`, count);
}

export function applyDirection(tag) {
  document.documentElement.dir = RTL_LANGS.has(tag) ? "rtl" : "ltr";
  document.documentElement.lang = tag;
}

export function applyTranslations(root = document) {
  root.querySelectorAll("[data-i18n]").forEach((el) => {
    el.textContent = t(el.dataset.i18n);
  });
  root.querySelectorAll("[data-i18n-placeholder]").forEach((el) => {
    el.placeholder = t(el.dataset.i18nPlaceholder);
  });
  root.querySelectorAll("[data-i18n-title]").forEach((el) => {
    el.title = t(el.dataset.i18nTitle);
  });
}
