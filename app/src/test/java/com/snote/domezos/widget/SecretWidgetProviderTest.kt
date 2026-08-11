package com.snote.domezos.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.compose.ui.graphics.toArgb
import androidx.test.core.app.ApplicationProvider
import com.snote.domezos.R
import com.snote.domezos.data.Prefs
import com.snote.domezos.ui.theme.ClassicTheme
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@Config(sdk = [34])
@RunWith(RobolectricTestRunner::class)
class SecretWidgetProviderTest {

    private lateinit var context: Context
    private lateinit var appWidgetManager: AppWidgetManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        appWidgetManager = AppWidgetManager.getInstance(context)
    }

    private fun createWidget(): Int = shadowOf(appWidgetManager).createWidget(
        SecretWidgetProvider::class.java,
        R.layout.widget_secret_encrypt
    )

    @Test
    fun `idle state shows hint text and hides result container when no link is saved`() {
        val appWidgetId = createWidget()
        Prefs.clearWidgetLink(context, appWidgetId)

        SecretWidgetProvider.updateWidget(context, appWidgetManager, appWidgetId)

        val root = shadowOf(appWidgetManager).getViewFor(appWidgetId)
        assertEquals(View.VISIBLE, root.findViewById<View>(R.id.idle_container).visibility)
        assertEquals(View.GONE, root.findViewById<View>(R.id.result_container).visibility)
        assertEquals(
            context.getString(R.string.hint_enter_text),
            root.findViewById<TextView>(R.id.widget_field_idle).text.toString()
        )
    }

    @Test
    fun `result state shows the saved link and hides idle container`() {
        val appWidgetId = createWidget()
        val savedLink = "https://domezos-ware.org?com=abc12"
        Prefs.setWidgetLink(context, appWidgetId, savedLink)

        SecretWidgetProvider.updateWidget(context, appWidgetManager, appWidgetId)

        val root = shadowOf(appWidgetManager).getViewFor(appWidgetId)
        assertEquals(View.GONE, root.findViewById<View>(R.id.idle_container).visibility)
        assertEquals(View.VISIBLE, root.findViewById<View>(R.id.result_container).visibility)
        assertEquals(savedLink, root.findViewById<TextView>(R.id.widget_field_result).text.toString())
    }

    @Test
    fun `applies the currently saved theme's background color`() {
        val appWidgetId = createWidget()
        Prefs.setTheme(context, "classic")
        Prefs.clearWidgetLink(context, appWidgetId)

        SecretWidgetProvider.updateWidget(context, appWidgetManager, appWidgetId)

        val root = shadowOf(appWidgetManager).getViewFor(appWidgetId)
        val background = root.findViewById<View>(R.id.widget_root).background as android.graphics.drawable.ColorDrawable
        assertEquals(ClassicTheme.colorScheme.background.toArgb(), background.color)
    }

    @Test
    fun `onDeleted clears the saved widget link for each removed id`() {
        val appWidgetId = 104
        Prefs.setWidgetLink(context, appWidgetId, "https://domezos-ware.org?com=abc12")

        SecretWidgetProvider().onDeleted(context, intArrayOf(appWidgetId))

        assertEquals(null, Prefs.getWidgetLink(context, appWidgetId))
    }
}
