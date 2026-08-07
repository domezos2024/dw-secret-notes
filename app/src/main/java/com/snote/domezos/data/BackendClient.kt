package com.snote.domezos.data

import android.content.Context
import android.util.Log
import com.snote.domezos.BuildConfig
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BackendClient {
    private const val TAG = "BackendClient"

    // Overridable only for tests (pointing at a local MockWebServer); production code never
    // changes this from the default.
    internal var BASE_URL = "https://domezos-ware.org/api/"
    private const val HMAC_SECRET = "change-me"
    private const val CONNECT_TIMEOUT_MS = 8_000
    private const val READ_TIMEOUT_MS = 8_000

    private fun openJsonPostConnection(path: String, extraHeaders: Map<String, String> = emptyMap()): HttpURLConnection {
        return (URL(BASE_URL + path).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            setRequestProperty("Content-Type", "application/json")
            extraHeaders.forEach { (key, value) -> setRequestProperty(key, value) }
        }
    }

    suspend fun performHandshake(ctx: Context): Boolean = withContext(Dispatchers.IO) {
        val deviceToken = Prefs.getDeviceToken(ctx)
        val timestamp = System.currentTimeMillis() / 1000
        val nonce = java.util.UUID.randomUUID().toString().take(8)
        val action = "handshake"
        val bodyJson = JSONObject().apply {
            put("action", action)
            put("timestamp", timestamp)
            put("nonce", nonce)
            put("device_token", deviceToken)
            put("app_version_code", BuildConfig.VERSION_CODE)
            put("app_version_name", BuildConfig.VERSION_NAME)
        }
        val bodyRaw = bodyJson.toString()
        val canonical = "$timestamp|$nonce|$action|$bodyRaw"
        val signature = hmacSha256(HMAC_SECRET, canonical)
        val conn = openJsonPostConnection(
            "encrypt_handshake.php",
            mapOf("X-Signature" to signature, "X-Device-Token" to deviceToken)
        )
        try {
            conn.outputStream.use { it.write(bodyRaw.toByteArray()) }
            if (conn.responseCode == 200) {
                val res = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(res)
                if (json.optString("status") == "ok") {
                    val data = json.getJSONObject("data")
                    val isPremium = data.optBoolean("premium_active", false)
                    Prefs.setPremium(ctx, isPremium)
                    return@withContext true
                }
            } else {
                Log.e(TAG, "Handshake failed: ${conn.responseCode} ${conn.responseMessage}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Handshake error", e)
        } finally {
            conn.disconnect()
        }
        false
    }

    private const val PREMIUM_API_KEY = "NMsePsV2026"

    suspend fun activatePremium(ctx: Context, productId: String): Boolean = withContext(Dispatchers.IO) {
        val deviceToken = Prefs.getDeviceToken(ctx)
        val conn = openJsonPostConnection("activate_premium.php")
        try {
            val body = JSONObject().apply {
                put("deviceToken", deviceToken)
                put("productId", productId)
                put("apiKey", PREMIUM_API_KEY)
            }.toString()
            conn.outputStream.use { it.write(body.toByteArray()) }
            if (conn.responseCode == 200) {
                val json = JSONObject(conn.inputStream.bufferedReader().readText())
                val ok = json.optBoolean("ok", false)
                if (ok) Prefs.setPremium(ctx, true)
                return@withContext ok
            }
        } catch (e: Exception) {
            Log.e(TAG, "activatePremium error", e)
        } finally {
            conn.disconnect()
        }
        false
    }

    suspend fun deactivatePremium(ctx: Context): Boolean = withContext(Dispatchers.IO) {
        val deviceToken = Prefs.getDeviceToken(ctx)
        val conn = openJsonPostConnection("deactivate_premium.php")
        try {
            val body = JSONObject().apply {
                put("deviceToken", deviceToken)
                put("apiKey", PREMIUM_API_KEY)
            }.toString()
            conn.outputStream.use { it.write(body.toByteArray()) }
            if (conn.responseCode == 200) {
                val json = JSONObject(conn.inputStream.bufferedReader().readText())
                val ok = json.optBoolean("ok", false)
                if (ok) {
                    Prefs.setPremium(ctx, false)
                    Prefs.setPremiumSource(ctx, "none")
                }
                return@withContext ok
            }
        } catch (e: Exception) {
            Log.e(TAG, "deactivatePremium error", e)
        } finally {
            conn.disconnect()
        }
        false
    }

    internal fun hmacSha256(key: String, data: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(key.toByteArray(), "HmacSHA256")
        mac.init(secretKey)
        return mac.doFinal(data.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}
