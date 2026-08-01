package com.snote.domezos.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.compose.ui.graphics.toArgb
import com.snote.domezos.MainActivity
import com.snote.domezos.R
import com.snote.domezos.data.Prefs
import com.snote.domezos.ui.theme.ALL_THEMES
import com.snote.domezos.ui.theme.ClassicTheme

class LauncherWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAll(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, LauncherWidgetProvider::class.java))
            for (id in ids) {
                updateWidget(context, appWidgetManager, id)
            }
        }

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_launcher)
            val launchPendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                Intent(context, MainActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_launcher_icon, launchPendingIntent)

            val savedThemeId = Prefs.getTheme(context)
            val theme = ALL_THEMES.find { it.id == savedThemeId } ?: ClassicTheme
            views.setInt(R.id.widget_launcher_root, "setBackgroundColor", theme.colorScheme.background.toArgb())

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
