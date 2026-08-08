import { decryptNote, BackendUnreachableError } from "./backend.js";
import { toImageSrc } from "./imageUtils.js";
import { THEMES, getSavedTheme, applyTheme, renderThemePicker } from "./theme.js";
import { LANGUAGES, getSavedLanguage, setSavedLanguage, loadLocale, t, tPlural, applyTranslations } from "./i18n.js";

const DECRYPT_COUNTDOWN_SECONDS = 60;

const $ = (id) => document.getElementById(id);

// Detect current page extension and normalize all internal links
function detectPageExtension() {
  const pathname = window.location.pathname;
  return pathname.endsWith('.php') || pathname.includes('.php/') ? 'php' : 'html';
}

function normalizeInternalLinks() {
  const ext = detectPageExtension();
  const otherExt = ext === 'php' ? 'html' : 'php';

  document.querySelectorAll('a[href]').forEach(link => {
    const href = link.getAttribute('href');
    if (href.includes(`index.${otherExt}`) || href.includes(`help.${otherExt}`) || href.includes(`info.${otherExt}`)) {
      const newHref = href.replace(`.${otherExt}`, `.${ext}`);
      link.setAttribute('href', newHref);
    }
  });
}

let countdownTimer = null;

function setStatus(el, message, kind) {
  el.textContent = message || "";
  el.classList.remove("success", "error");
  if (kind) el.classList.add(kind);
}

/* ---------- Settings (language / theme) ---------- */

function renderLanguagePicker() {
  const container = $("languagePicker");
  const current = getSavedLanguage();
  container.innerHTML = "";
  for (const lang of LANGUAGES) {
    const btn = document.createElement("button");
    btn.type = "button";
    btn.className = "picker-item";
    btn.setAttribute("aria-pressed", String(lang.tag === current));
    btn.innerHTML = `<span class="names"><span class="native-name">${lang.native}</span><span class="english-name">${lang.english}</span></span>`;
    btn.addEventListener("click", async () => {
      setSavedLanguage(lang.tag);
      await loadLocale(lang.tag);
      applyTranslations();
      renderLanguagePicker();
      renderThemePickerLocalized();
    });
    container.appendChild(btn);
  }
}

function renderThemePickerLocalized() {
  renderThemePicker($("themePicker"), (id) => t(`theme_${id}`), () => {});
}

$("settingsToggle").addEventListener("click", () => {
  $("settingsCard").classList.toggle("hidden");
});

/* ---------- Decrypt-from-link flow ---------- */

async function decryptFromLink(alias, pass) {
  setStatus($("decryptStatus"), t("label_decrypting"));

  try {
    const result = await decryptNote(alias, pass, false);
    if (result.ok) {
      showDecryptedResult(result.text, result.image);
    } else {
      const key = result.reason === "not_found" ? "error_not_found" : "error_decrypt_failed";
      setStatus($("decryptStatus"), t(key), "error");
    }
  } catch (err) {
    if (err instanceof BackendUnreachableError) {
      setStatus($("decryptStatus"), t("error_network"), "error");
    } else {
      console.error(err);
      setStatus($("decryptStatus"), String(err), "error");
    }
  }
}

function showDecryptedResult(text, image) {
  $("resultCard").classList.remove("hidden");
  $("destroyedBanner").classList.add("hidden");

  if (text) {
    $("secretText").textContent = text;
    $("secretText").classList.remove("hidden");
  } else {
    $("secretText").classList.add("hidden");
  }
  if (image) {
    $("secretImage").src = toImageSrc(image);
    $("secretImage").classList.remove("hidden");
  } else {
    $("secretImage").classList.add("hidden");
  }

  setStatus($("decryptStatus"), t("success_decrypted"), "success");
  startCountdown();
}

function startCountdown() {
  let remaining = DECRYPT_COUNTDOWN_SECONDS;
  const banner = $("countdownBanner");
  banner.classList.remove("hidden");
  banner.textContent = tPlural("note_auto_delete", remaining);

  clearInterval(countdownTimer);
  countdownTimer = setInterval(() => {
    remaining -= 1;
    if (remaining <= 0) {
      clearInterval(countdownTimer);
      finishCountdown();
      return;
    }
    banner.textContent = tPlural("note_auto_delete", remaining);
  }, 1000);
}

function finishCountdown() {
  $("secretText").classList.add("hidden");
  $("secretText").textContent = "";
  $("secretImage").classList.add("hidden");
  $("secretImage").src = "";
  $("countdownBanner").classList.add("hidden");
  $("destroyedBanner").classList.remove("hidden");
}

$("backBtn").addEventListener("click", () => {
  window.location.href = detectPageExtension() === "php" ? "../index.php" : "../index.html";
});

/* ---------- Init ---------- */

async function init() {
  normalizeInternalLinks();
  applyTheme(getSavedTheme());
  await loadLocale(getSavedLanguage());
  applyTranslations();
  renderLanguagePicker();
  renderThemePickerLocalized();

  const params = new URLSearchParams(window.location.search);
  const com = params.get("com");
  const pass = params.get("pass") || "";

  if (com) {
    await decryptFromLink(com, pass);
  } else {
    setStatus($("decryptStatus"), t("error_invalid_link"), "error");
    $("backBtn").style.display = "block";
  }
}

init();
