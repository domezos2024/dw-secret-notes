/**
 * Theme system ported from AppThemes.kt. The actual color values live in
 * css/themes.css as [data-theme="id"] custom-property blocks; this module
 * only tracks the id list (+ a swatch color for the picker), persistence,
 * and applying the chosen theme to <html data-theme="...">.
 */

export const THEMES = [
  { id: "classic", swatch: "#00d4ff" },
  { id: "dark", swatch: "#00d4ff" },
  { id: "light", swatch: "#0077a6" },
  { id: "midnight", swatch: "#bb86fc" },
  { id: "forest", swatch: "#81c784" },
  { id: "ocean", swatch: "#4fc3f7" },
  { id: "cyberpunk", swatch: "#fcee09" },
  { id: "dracula", swatch: "#bd93f9" },
  { id: "sunset", swatch: "#ff7043" },
  { id: "nordic", swatch: "#88c0d0" },
  { id: "matrix", swatch: "#00ff41" },
  { id: "sakura", swatch: "#ffb7c5" },
  { id: "golden", swatch: "#d4af37" },
  { id: "ruby", swatch: "#e0115f" },
  { id: "electric", swatch: "#b44dff" },
  { id: "ghost", swatch: "#98989d" },
  { id: "solarized", swatch: "#268bd2" },
];

const STORAGE_KEY = "dw_theme";
const DEFAULT_THEME = "classic";

export function getSavedTheme() {
  const saved = localStorage.getItem(STORAGE_KEY);
  return THEMES.some((t) => t.id === saved) ? saved : DEFAULT_THEME;
}

export function saveTheme(themeId) {
  localStorage.setItem(STORAGE_KEY, themeId);
}

export function applyTheme(themeId) {
  document.documentElement.dataset.theme = themeId;
}

/**
 * @param {HTMLElement} containerEl
 * @param {(themeId: string) => string} labelFor  translated label for a theme id
 * @param {(themeId: string) => void} onChange
 */
export function renderThemePicker(containerEl, labelFor, onChange) {
  const current = getSavedTheme();
  containerEl.innerHTML = "";
  containerEl.setAttribute("role", "listbox");

  for (const theme of THEMES) {
    const btn = document.createElement("button");
    btn.type = "button";
    btn.className = "picker-item";
    btn.setAttribute("aria-pressed", String(theme.id === current));
    btn.innerHTML = `<span class="swatch" style="background:${theme.swatch}"></span><span class="names"><span class="native-name"></span></span>`;
    btn.querySelector(".native-name").textContent = labelFor(theme.id);
    btn.addEventListener("click", () => {
      saveTheme(theme.id);
      applyTheme(theme.id);
      containerEl.querySelectorAll(".picker-item").forEach((el) => el.setAttribute("aria-pressed", "false"));
      btn.setAttribute("aria-pressed", "true");
      onChange(theme.id);
    });
    containerEl.appendChild(btn);
  }
}
