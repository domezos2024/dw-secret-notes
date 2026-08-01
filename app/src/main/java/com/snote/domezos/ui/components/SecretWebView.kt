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
                "https://domezos-ware.org/api/android_be_encrypt.php",
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
            val isLink = alias.startsWith("link:")
            val cleanAlias = if (isLink) alias.substring(5) else alias
            val encodedPass = URLEncoder.encode(pass, "UTF-8")
            val url = if (isLink) {
                "https://snote.fun?link=$cleanAlias"
            } else {
                val encodedAlias = URLEncoder.encode(cleanAlias, "UTF-8")
                "https://domezos-ware.org/api/view_api.php?com=$encodedAlias&pass=$encodedPass"
            }
            loadUrl(url)
        } catch (e: Exception) {
            callback("Error: ${e.message}")
        }
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
