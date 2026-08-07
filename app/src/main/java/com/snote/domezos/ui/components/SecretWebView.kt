package com.snote.domezos.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.snote.domezos.data.Prefs
import org.json.JSONObject
import java.net.URLEncoder

@SuppressLint("ViewConstructor")
class SecretWebView(context: Context) : WebView(context) {

    private var onResult: ((String) -> Unit)? = null
    private var onImageResult: ((String) -> Unit)? = null

    init {
        setupWebView()
    }

    companion object {
        private const val ENCRYPT_ENDPOINT = "https://domezos-ware.org/api/android_be_encrypt.php"
        private const val DECRYPT_ENDPOINT = "https://domezos-ware.org/api/view_api.php"
        private const val SHORT_LINK_HOST = "https://snote.fun"
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        addJavascriptInterface(AndroidInterface(), "Android")
        webViewClient = WebViewClient()
    }

    fun encrypt(text: String, imageBase64: String? = null, callback: (String) -> Unit) {
        this.onResult = callback
        try {
            val body = buildString {
                append("write=").append(URLEncoder.encode(text, "UTF-8"))
                if (!imageBase64.isNullOrEmpty()) {
                    append("&image=").append(URLEncoder.encode(imageBase64, "UTF-8"))
                }
            }
            postUrl(
                ENCRYPT_ENDPOINT,
                body.toByteArray(Charsets.UTF_8)
            )
        } catch (e: Exception) {
            callback("Error: ${e.message}")
        }
    }

    fun decrypt(
        alias: String,
        pass: String,
        callback: (String) -> Unit,
        onImage: ((String) -> Unit)? = null
    ) {
        this.onResult = callback
        this.onImageResult = onImage
        try {
            loadUrl(buildDecryptUrl(alias, pass))
        } catch (e: Exception) {
            callback("Error: ${e.message}")
        }
    }

    private fun buildDecryptUrl(alias: String, pass: String): String {
        val isLink = alias.startsWith("link:")
        val cleanAlias = if (isLink) alias.substring(5) else alias
        if (isLink) {
            return "$SHORT_LINK_HOST?link=$cleanAlias"
        }
        val encodedAlias = URLEncoder.encode(cleanAlias, "UTF-8")
        val encodedPass = URLEncoder.encode(pass, "UTF-8")
        return "$DECRYPT_ENDPOINT?com=$encodedAlias&pass=$encodedPass"
    }

    inner class AndroidInterface {
        @JavascriptInterface
        fun getToken(): String = Prefs.getDeviceToken(context)

        @JavascriptInterface
        fun notifyDataReady(json: String) {
            try {
                val url = JSONObject(json).optString("url")
                post { onResult?.invoke(url) }
            } catch (e: Exception) {
                post { onResult?.invoke("Error: Invalid response from backend") }
            }
        }

        @JavascriptInterface
        fun sendAnswer(result: String) {
            post { onResult?.invoke(result) }
        }

        @JavascriptInterface
        fun sendImage(base64: String) {
            post { onImageResult?.invoke(base64) }
        }
    }
}
