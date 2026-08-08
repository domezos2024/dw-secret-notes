import { parseAliasFromInput } from "./aliasParser.js";
import { resizeImageToBase64 } from "./imageUtils.js";
import { toImageSrc } from "./imageUtils.js";
import { encryptNote, decryptNote, BackendUnreachableError } from "./backend.js";
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

let pendingImageBase64 = null;
let countdownTimer = null;

function showToast(message) {
  const toast = $("toast");
  toast.textContent = message;
  toast.classList.add("show");
  clearTimeout(showToast._t);
  showToast._t = setTimeout(() => toast.classList.remove("show"), 3000);
}

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

/* ---------- Tabs ---------- */

function selectTab(which) {
  const isEncrypt = which === "encrypt";
  $("tabEncryptBtn").setAttribute("aria-selected", String(isEncrypt));
  $("tabDecryptBtn").setAttribute("aria-selected", String(!isEncrypt));
  $("encryptPanel").classList.toggle("active", isEncrypt);
  $("decryptPanel").classList.toggle("active", !isEncrypt);
}

$("tabEncryptBtn").addEventListener("click", () => selectTab("encrypt"));
$("tabDecryptBtn").addEventListener("click", () => selectTab("decrypt"));

/* ---------- Encrypt ---------- */

$("imageInput").addEventListener("change", async () => {
  const file = $("imageInput").files[0];
  if (!file) return;
  try {
    pendingImageBase64 = await resizeImageToBase64(file);
    $("imagePreviewImg").src = toImageSrc(pendingImageBase64);
    $("imagePreview").classList.add("active");
    $("encryptText").placeholder = t("hint_enter_text_optional");
  } catch (err) {
    console.error(err);
    pendingImageBase64 = null;
  }
});

$("removeImageBtn").addEventListener("click", () => {
  pendingImageBase64 = null;
  $("imageInput").value = "";
  $("imagePreview").classList.remove("active");
  $("encryptText").placeholder = t("hint_enter_text");
});

$("encryptBtn").addEventListener("click", async () => {
  const rawText = $("encryptText").value.trim();
  if (!rawText && !pendingImageBase64) return;

  $("encryptBtn").disabled = true;
  setStatus($("encryptStatus"), "…");

  try {
    const result = await encryptNote(rawText, pendingImageBase64);
    if (result.ok) {
      $("resultLink").textContent = result.link;
      $("aliasChip").textContent = formatGeneratedAlias(result.link);
      $("encryptResultCard").classList.remove("hidden");
      setStatus($("encryptStatus"), "");
      $("encryptText").value = "";
      pendingImageBase64 = null;
      $("imageInput").value = "";
      $("imagePreview").classList.remove("active");
    } else {
      setStatus($("encryptStatus"), t("error_network"), "error");
    }
  } catch (err) {
    if (err instanceof BackendUnreachableError) {
      setStatus($("encryptStatus"), t("error_network"), "error");
    } else {
      console.error(err);
      setStatus($("encryptStatus"), String(err), "error");
    }
  } finally {
    $("encryptBtn").disabled = false;
  }
});

function formatGeneratedAlias(link) {
  if (link.includes("com=")) {
    return link.split("com=")[1].split("&")[0].slice(0, 12) + "...";
  }
  if (link.includes("link=")) {
    return link.split("link=")[1].slice(0, 8);
  }
  return "Link Ready";
}

$("copyLinkBtn").addEventListener("click", async () => {
  const link = $("resultLink").textContent;
  try {
    await navigator.clipboard.writeText(link);
    showToast(t("snackbar_link_copied"));
  } catch {
    showToast(t("snackbar_link_copied"));
  }
});

$("shareLinkBtn").addEventListener("click", async () => {
  const link = $("resultLink").textContent;
  if (navigator.share) {
    try {
      await navigator.share({ url: link, text: link });
    } catch {
      /* user cancelled */
    }
  } else {
    try {
      await navigator.clipboard.writeText(link);
      showToast(t("snackbar_link_copied"));
    } catch {
      showToast(t("error_share_unavailable"));
    }
  }
});

$("newMessageBtn").addEventListener("click", () => {
  $("encryptResultCard").classList.add("hidden");
  $("encryptText").value = "";
  $("encryptText").placeholder = t("hint_enter_text");
});

/* ---------- Decrypt ---------- */

async function runDecrypt(rawInput) {
  const parsed = parseAliasFromInput(rawInput);
  if (!parsed) {
    setStatus($("decryptStatus"), t("error_invalid_link"), "error");
    return;
  }
  await decryptParsed(parsed);
}

async function decryptParsed(parsed) {
  $("decryptBtn").disabled = true;
  setStatus($("decryptStatus"), t("label_decrypting"));

  try {
    const result = await decryptNote(parsed.alias, parsed.pass, parsed.isShortLink);
    if (result.ok) {
      showDecryptedResult(result.text, result.image);
      setStatus($("decryptStatus"), "");
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
  } finally {
    $("decryptBtn").disabled = false;
  }
}

$("decryptBtn").addEventListener("click", () => runDecrypt($("decryptInput").value));

function showDecryptedResult(text, image) {
  $("decryptFormCard").classList.add("hidden");
  $("decryptResultCard").classList.remove("hidden");
  $("destroyedBanner").classList.add("hidden");
  $("readAnotherBtn").classList.add("hidden");

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

  showToast(t("success_decrypted"));
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
  $("readAnotherBtn").classList.remove("hidden");
}

$("readAnotherBtn").addEventListener("click", () => {
  $("decryptResultCard").classList.add("hidden");
  $("decryptFormCard").classList.remove("hidden");
  $("decryptInput").value = "";
  setStatus($("decryptStatus"), "");
});

/* ---------- Deep-link handling (?com=&pass= / ?link=) ---------- */

function checkDeepLink() {
  const params = new URLSearchParams(window.location.search);
  const com = params.get("com");
  const link = params.get("link");
  if (com) {
    selectTab("decrypt");
    $("decryptInput").value = com;
    decryptParsed({ alias: com, pass: params.get("pass") || "", isShortLink: false });
  } else if (link) {
    selectTab("decrypt");
    $("decryptInput").value = link;
    decryptParsed({ alias: link, pass: "", isShortLink: true });
  }
}

/* ---------- Init ---------- */

async function init() {
  normalizeInternalLinks();
  applyTheme(getSavedTheme());
  await loadLocale(getSavedLanguage());
  applyTranslations();
  renderLanguagePicker();
  renderThemePickerLocalized();
  checkDeepLink();
}

init();
