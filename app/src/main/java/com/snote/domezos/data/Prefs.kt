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
    fun getTipCount(ctx: Context): Int = prefs(ctx).getInt("tip_count", 0)
    fun incrementTipCount(ctx: Context, amount: Int = 1) {
        prefs(ctx).edit { putInt("tip_count", getTipCount(ctx) + amount) }
    }
    fun getWidgetLink(ctx: Context, appWidgetId: Int): String? = prefs(ctx).getString("widget_link_$appWidgetId", null)
    fun setWidgetLink(ctx: Context, appWidgetId: Int, link: String) {
        prefs(ctx).edit { putString("widget_link_$appWidgetId", link) }
    }
    fun clearWidgetLink(ctx: Context, appWidgetId: Int) {
        prefs(ctx).edit { remove("widget_link_$appWidgetId") }
    }
    fun hasRatedApp(ctx: Context): Boolean = prefs(ctx).getBoolean("has_rated_app", false)
    fun setHasRatedApp(ctx: Context, rated: Boolean) {
        prefs(ctx).edit { putBoolean("has_rated_app", rated) }
    }
    fun getRunCount(ctx: Context): Int = prefs(ctx).getInt("run_count", 0)
    fun incrementRunCount(ctx: Context): Int {
        val next = getRunCount(ctx) + 1
        prefs(ctx).edit { putInt("run_count", next) }
        return next
    }
    fun hasSeenRatePrompt(ctx: Context): Boolean = prefs(ctx).getBoolean("has_seen_rate_prompt", false)
    fun setHasSeenRatePrompt(ctx: Context) {
        prefs(ctx).edit { putBoolean("has_seen_rate_prompt", true) }
    }
}
