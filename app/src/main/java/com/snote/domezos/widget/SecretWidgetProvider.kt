package com.snote.domezos.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.compose.ui.graphics.toArgb
import com.snote.domezos.R
import com.snote.domezos.data.LocaleManager
import com.snote.domezos.data.Prefs
import com.snote.domezos.ui.theme.ALL_THEMES
import com.snote.domezos.ui.theme.ClassicTheme

class SecretWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            Prefs.clearWidgetLink(context, appWidgetId)
        }
    }

    companion object {
        private const val SHARE_REQUEST_CODE_OFFSET = 100_000

        fun updateAll(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, SecretWidgetProvider::class.java))
            for (id in ids) {
                updateWidget(context, appWidgetManager, id)
            }
        }

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val localizedContext = LocaleManager.applyLocale(context)
            val views = RemoteViews(context.packageName, R.layout.widget_secret_encrypt)
            val link = Prefs.getWidgetLink(context, appWidgetId)

            val savedThemeId = Prefs.getTheme(context)
            val theme = ALL_THEMES.find { it.id == savedThemeId } ?: ClassicTheme
            views.setInt(R.id.widget_root, "setBackgroundColor", theme.colorScheme.background.toArgb())
            views.setInt(R.id.widget_field_idle, "setTextColor", theme.colorScheme.onSurface.toArgb())
            views.setInt(R.id.widget_field_result, "setTextColor", theme.colorScheme.onSurface.toArgb())
            views.setInt(R.id.widget_btn_encrypt, "setBackgroundColor", theme.colorScheme.secondary.toArgb())
            views.setInt(R.id.widget_btn_encrypt, "setTextColor", theme.colorScheme.onSecondary.toArgb())
            views.setInt(R.id.widget_btn_share, "setBackgroundColor", theme.colorScheme.secondary.toArgb())
            views.setInt(R.id.widget_btn_share, "setTextColor", theme.colorScheme.onSecondary.toArgb())

            val encryptPendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                Intent(context, WidgetEncryptActivity::class.java)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (link == null) {
                views.setViewVisibility(R.id.idle_container, android.view.View.VISIBLE)
                views.setViewVisibility(R.id.result_container, android.view.View.GONE)
                views.setTextViewText(R.id.widget_field_idle, localizedContext.getString(R.string.hint_enter_text))
                views.setTextViewText(R.id.widget_btn_encrypt, localizedContext.getString(R.string.btn_encrypt))
                views.setOnClickPendingIntent(R.id.widget_field_idle, encryptPendingIntent)
                views.setOnClickPendingIntent(R.id.widget_btn_encrypt, encryptPendingIntent)
            } else {
                views.setViewVisibility(R.id.idle_container, android.view.View.GONE)
                views.setViewVisibility(R.id.result_container, android.view.View.VISIBLE)
                views.setTextViewText(R.id.widget_field_result, link)
                views.setTextViewText(R.id.widget_btn_share, localizedContext.getString(R.string.btn_share))
                views.setOnClickPendingIntent(R.id.widget_field_result, encryptPendingIntent)

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, localizedContext.getString(R.string.share_message, link))
                }
                val chooserIntent = Intent.createChooser(shareIntent, localizedContext.getString(R.string.share_title)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val sharePendingIntent = PendingIntent.getActivity(
                    context,
                    appWidgetId + SHARE_REQUEST_CODE_OFFSET,
                    chooserIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_btn_share, sharePendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
