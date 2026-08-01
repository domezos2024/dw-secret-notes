package com.snote.domezos.widget

import android.content.Context

/** Re-renders all installed home-screen widgets, e.g. after the active theme changes. */
object WidgetRefresher {
    fun refreshAll(context: Context) {
        SecretWidgetProvider.updateAll(context)
        LauncherWidgetProvider.updateAll(context)
    }
}
