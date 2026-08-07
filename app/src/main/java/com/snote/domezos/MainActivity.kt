package com.snote.domezos

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.lifecycleScope
import com.snote.domezos.billing.BillingHelper
import com.snote.domezos.data.BackendClient
import com.snote.domezos.data.LocaleManager
import com.snote.domezos.data.Prefs
import com.snote.domezos.navigation.AppNavigation
import com.snote.domezos.ui.theme.DwSecretNotesTheme
import com.snote.domezos.ui.theme.ALL_THEMES
import com.snote.domezos.ui.theme.ClassicTheme
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    private var deepLinkAlias: String? by mutableStateOf(null)
    private var isPremium by mutableStateOf(false)
    private var billingHelper: BillingHelper? = null

    companion object {
        private val URL_REGEX   = """(https?://\S+)""".toRegex()
        private val ALIAS_REGEX = """\b([A-Za-z0-9]{5})\b""".toRegex()
        private const val RATE_PROMPT_RUN_COUNT = 3
    }

    fun clearDeepLink() {
        deepLinkAlias = null
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.applyLocale(base))
    }

    private fun refreshPremiumFromServer() {
        lifecycleScope.launch {
            BackendClient.performHandshake(this@MainActivity)
            isPremium = Prefs.isPremium(this@MainActivity)
            billingHelper?.syncSubscriptions()
        }
    }

    private fun setupSecureFlagsIfNeeded() {
        if (!BuildConfig.DEBUG) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    private fun computeShowRatePrompt(runCount: Int): Boolean =
        runCount == RATE_PROMPT_RUN_COUNT && !Prefs.hasRatedApp(this) && !Prefs.hasSeenRatePrompt(this)

    private fun createBillingHelper(): BillingHelper = BillingHelper(
        context = this,
        onPremiumActivated = {
            Prefs.setPremium(this, true)
            isPremium = true
        },
        onPremiumDeactivated = {
            Prefs.setPremium(this, false)
            isPremium = false
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        setupSecureFlagsIfNeeded()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val isFirstRun = Prefs.getLanguage(this) == null
        val runCount = Prefs.incrementRunCount(this)
        val showRatePrompt = computeShowRatePrompt(runCount)
        deepLinkAlias = extractAliasFromIntent(intent)
        isPremium = Prefs.isPremium(this)
        billingHelper = createBillingHelper()
        refreshPremiumFromServer()
        setContent {
            val currentThemeId = remember { mutableStateOf(Prefs.getTheme(this)) }
            val themeConfig = ALL_THEMES.find { it.id == currentThemeId.value } ?: ClassicTheme
            LaunchedEffect(themeConfig) {
                window.setBackgroundDrawable(ColorDrawable(themeConfig.colorScheme.background.toArgb()))
            }
            DwSecretNotesTheme(themeConfig = themeConfig) {
                AppNavigation(
                    startWithLanguagePicker = isFirstRun,
                    showRatePrompt = showRatePrompt,
                    initialDecryptInput = deepLinkAlias,
                    onDeepLinkConsumed = { clearDeepLink() },
                    onThemeChanged = { newThemeId ->
                        Prefs.setTheme(this, newThemeId)
                        currentThemeId.value = newThemeId
                    },
                    currentThemeId = currentThemeId.value,
                    isPremium = isPremium,
                    onPremiumChanged = { active ->
                        Prefs.setPremium(this, active)
                        isPremium = active
                    },
                    billingHelper = billingHelper
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        billingHelper?.destroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val newAlias = extractAliasFromIntent(intent)
        if (newAlias != null) deepLinkAlias = newAlias
        refreshPremiumFromServer()
    }

    private fun extractAliasFromIntent(intent: Intent?): String? {
        val data = intent?.data
        if (data != null) return extractAliasFromUri(data)
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return null
            return extractAliasFromShareText(text)
        }
        return null
    }

    private fun extractAliasFromShareText(text: String): String? {
        val urlMatch = URL_REGEX.find(text)
        if (urlMatch != null) {
            val uri = try { urlMatch.value.toUri() } catch (_: Exception) { null }
            if (uri != null) {
                val alias = extractAliasFromUri(uri)
                if (alias != null) return alias
            }
            return urlMatch.value
        }
        return ALIAS_REGEX.find(text)?.value ?: text
    }

    private fun extractAliasFromUri(data: android.net.Uri): String? {
        return when (data.host) {
            "domezos-ware.org" -> {
                val com = data.getQueryParameter("com") ?: return null
                val pass = data.getQueryParameter("pass")
                if (!pass.isNullOrEmpty()) "$com|$pass" else com
            }
            "snote.fun" -> data.getQueryParameter("link")
            else -> null
        }
    }
}
