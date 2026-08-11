package com.snote.domezos.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Robolectric ships shadows only up to SDK 34 at this dependency version; pin to it since
// this project's compileSdk/targetSdk (37) outruns Robolectric's supported range.
@Config(sdk = [34])
@RunWith(RobolectricTestRunner::class)
class PrefsTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `language defaults to null when never set`() {
        assertNull(Prefs.getLanguage(context))
    }

    @Test
    fun `language persists after being set`() {
        Prefs.setLanguage(context, "de")
        assertEquals("de", Prefs.getLanguage(context))
    }

    @Test
    fun `theme defaults to classic`() {
        assertEquals("classic", Prefs.getTheme(context))
    }

    @Test
    fun `device token is generated once and stable across calls`() {
        val first = Prefs.getDeviceToken(context)
        val second = Prefs.getDeviceToken(context)
        assertEquals(first, second)
        assertTrue(first.endsWith("-app"))
    }


    @Test
    fun `widget link is scoped per widget id`() {
        Prefs.setWidgetLink(context, appWidgetId = 1, link = "https://snote.fun?link=abc12")
        Prefs.setWidgetLink(context, appWidgetId = 2, link = "https://snote.fun?link=xyz99")

        assertEquals("https://snote.fun?link=abc12", Prefs.getWidgetLink(context, 1))
        assertEquals("https://snote.fun?link=xyz99", Prefs.getWidgetLink(context, 2))

        Prefs.clearWidgetLink(context, 1)
        assertNull(Prefs.getWidgetLink(context, 1))
        assertEquals("https://snote.fun?link=xyz99", Prefs.getWidgetLink(context, 2))
    }

    @Test
    fun `run count increments and returns the new value`() {
        assertEquals(0, Prefs.getRunCount(context))
        assertEquals(1, Prefs.incrementRunCount(context))
        assertEquals(2, Prefs.incrementRunCount(context))
        assertEquals(2, Prefs.getRunCount(context))
    }

    @Test
    fun `has rated app defaults to false and can be set`() {
        assertFalse(Prefs.hasRatedApp(context))
        Prefs.setHasRatedApp(context, true)
        assertTrue(Prefs.hasRatedApp(context))
    }

    @Test
    fun `has seen rate prompt defaults to false and can only be set to true`() {
        assertFalse(Prefs.hasSeenRatePrompt(context))
        Prefs.setHasSeenRatePrompt(context)
        assertTrue(Prefs.hasSeenRatePrompt(context))
    }
}
