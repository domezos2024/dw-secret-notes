/** Single source of truth for the backend host, shared by all WebApp modules. */
export const APP_HOST = "domezos-ware.com";
export const APP_BASE_URL = `https://${APP_HOST}`;

// Identifies this web app's own traffic to the backend's rate limiter
// (see WebApp/api/auth.php). Not a security boundary — it's public JS,
// visible to anyone via view-source. Its only job is a generous quota
// tier for first-party traffic, separate from third-party API users.
export const APP_API_KEY = "51dd1a7d88df706213b7529a4bbe764867ae09cb89ecec73";
