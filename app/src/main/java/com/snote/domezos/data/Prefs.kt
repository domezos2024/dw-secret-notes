package com.snote.domezos.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object Prefs {
    private const val FILE = "dw_prefs"
    private const val KEY_LANG = "selected_language"
    private const val KEY_THEME = "selected_theme"
    private const val KEY_DEVICE_TOKEN = "device_token"
    private const val KEY_IS_PREMIUM = "is_premium"
    private const val KEY_PREMIUM_SOURCE = "premium_source" // "none", "inapp", "subscription"
    private const val KEY_TIP_COUNT = "tip_count"
    private const val KEY_WIDGET_LINK_PREFIX = "widget_link_"
    private const val KEY_HAS_RATED_APP = "has_rated_app"
    private const val KEY_RUN_COUNT = "run_count"
    private const val KEY_HAS_SEEN_RATE_PROMPT = "has_seen_rate_prompt"
    private fun prefs(ctx: Context): SharedPreferences = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
    fun getLanguage(ctx: Context): String? = prefs(ctx).getString(KEY_LANG, null)
    fun setLanguage(ctx: Context, tag: String) {
        prefs(ctx).edit { putString(KEY_LANG, tag) }
    }
    fun getTheme(ctx: Context): String = prefs(ctx).getString(KEY_THEME, "classic") ?: "classic"
    fun setTheme(ctx: Context, theme: String) {
        prefs(ctx).edit { putString(KEY_THEME, theme) }
        com.snote.domezos.widget.WidgetRefresher.refreshAll(ctx)
    }
    fun getDeviceToken(ctx: Context): String {
        val p = prefs(ctx)
        var token = p.getString(KEY_DEVICE_TOKEN, null)
        if (token == null) {
            val uuid = java.util.UUID.randomUUID().toString()
            token = "$uuid-app"
            p.edit { putString(KEY_DEVICE_TOKEN, token) }
        }
        return token
    }
    fun isPremium(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_IS_PREMIUM, false)
    fun setPremium(ctx: Context, active: Boolean) {
        prefs(ctx).edit { putBoolean(KEY_IS_PREMIUM, active) }
    }
    fun getPremiumSource(ctx: Context): String = prefs(ctx).getString(KEY_PREMIUM_SOURCE, "none") ?: "none"
    fun setPremiumSource(ctx: Context, source: String) {
        prefs(ctx).edit { putString(KEY_PREMIUM_SOURCE, source) }
    }
    fun getTipCount(ctx: Context): Int = prefs(ctx).getInt(KEY_TIP_COUNT, 0)
    fun incrementTipCount(ctx: Context, amount: Int = 1) {
        prefs(ctx).edit { putInt(KEY_TIP_COUNT, getTipCount(ctx) + amount) }
    }
    fun getWidgetLink(ctx: Context, appWidgetId: Int): String? = prefs(ctx).getString("$KEY_WIDGET_LINK_PREFIX$appWidgetId", null)
    fun setWidgetLink(ctx: Context, appWidgetId: Int, link: String) {
        prefs(ctx).edit { putString("$KEY_WIDGET_LINK_PREFIX$appWidgetId", link) }
    }
    fun clearWidgetLink(ctx: Context, appWidgetId: Int) {
        prefs(ctx).edit { remove("$KEY_WIDGET_LINK_PREFIX$appWidgetId") }
    }
    fun hasRatedApp(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_HAS_RATED_APP, false)
    fun setHasRatedApp(ctx: Context, rated: Boolean) {
        prefs(ctx).edit { putBoolean(KEY_HAS_RATED_APP, rated) }
    }
    fun getRunCount(ctx: Context): Int = prefs(ctx).getInt(KEY_RUN_COUNT, 0)
    fun incrementRunCount(ctx: Context): Int {
        val next = getRunCount(ctx) + 1
        prefs(ctx).edit { putInt(KEY_RUN_COUNT, next) }
        return next
    }
    fun hasSeenRatePrompt(ctx: Context): Boolean = prefs(ctx).getBoolean(KEY_HAS_SEEN_RATE_PROMPT, false)
    fun setHasSeenRatePrompt(ctx: Context) {
        prefs(ctx).edit { putBoolean(KEY_HAS_SEEN_RATE_PROMPT, true) }
    }
}
