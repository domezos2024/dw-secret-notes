/**
 * Port of ImageUtils.kt: downscale so the longest edge is <= MAX_EDGE
 * (never upscale), encode as JPEG at JPEG_QUALITY, and strip the "data:"
 * prefix before upload — the backend expects raw base64 like the Android
 * WebView bridge sends.
 */

const MAX_EDGE = 1600;
const JPEG_QUALITY = 0.8;

function loadImageFromFile(file) {
  return new Promise((resolve, reject) => {
    const url = URL.createObjectURL(file);
    const img = new Image();
    img.onload = () => {
      URL.revokeObjectURL(url);
      resolve(img);
    };
    img.onerror = (err) => {
      URL.revokeObjectURL(url);
      reject(err);
    };
    img.src = url;
  });
}

function computeScaledDimensions(width, height, maxEdge = MAX_EDGE) {
  const longestEdge = Math.max(width, height);
  if (longestEdge <= maxEdge) {
    return { width, height };
  }
  const scale = maxEdge / longestEdge;
  return {
    width: Math.round(width * scale),
    height: Math.round(height * scale),
  };
}

/** @returns {Promise<string>} base64 JPEG string, no "data:" prefix */
export async function resizeImageToBase64(file) {
  const img = await loadImageFromFile(file);
  const { width, height } = computeScaledDimensions(img.naturalWidth, img.naturalHeight);

  const canvas = document.createElement("canvas");
  canvas.width = width;
  canvas.height = height;
  const ctx = canvas.getContext("2d");
  ctx.drawImage(img, 0, 0, width, height);

  const dataUrl = canvas.toDataURL("image/jpeg", JPEG_QUALITY);
  return dataUrl.substring(dataUrl.indexOf(",") + 1);
}

/** Tolerates an optional "data:...;base64," prefix, mirrors ImageUtils.bitmapFromBase64. */
export function cleanBase64(raw) {
  if (!raw) return "";
  const commaIndex = raw.indexOf(",");
  return commaIndex >= 0 ? raw.substring(commaIndex + 1) : raw;
}

export function toImageSrc(raw) {
  return `data:image/jpeg;base64,${cleanBase64(raw)}`;
}
