package com.snote.domezos.data

/** Single source of truth for the backend host used across deep links, endpoints and UI. */
object Backend {
    const val HOST = "domezos-ware.com"
    const val BASE_URL = "https://$HOST"

    // Identifies this app's own traffic to the backend's rate limiter (see WebApp/api/auth.php).
    // Not a security boundary — it ships inside the APK and is trivially extractable, same as
    // this being an open-source repo. Its only job is to give the app a generous quota tier
    // separate from third-party API users, not to gate access to anything sensitive.
    const val API_KEY = "53d1dc1f01dbe3dce66f53609ffa7f42ed572311414e87c0"
}
