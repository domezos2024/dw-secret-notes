package com.snote.domezos.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [34])
@RunWith(RobolectricTestRunner::class)
class BackendClientNetworkTest {

    private lateinit var server: MockWebServer
    private lateinit var context: Context
    private val originalBaseUrl = "https://domezos-ware.org/api/"

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        BackendClient.BASE_URL = server.url("/").toString()
        context = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
        server.shutdown()
        BackendClient.BASE_URL = originalBaseUrl
    }

    @Test
    fun `performHandshake activates premium on ok response with premium_active true`() = runTest {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"status":"ok","data":{"premium_active":true}}"""
            )
        )

        val result = BackendClient.performHandshake(context)

        assertTrue(result)
        assertTrue(Prefs.isPremium(context))

        val recorded = server.takeRequest()
        assertEquals("/encrypt_handshake.php", recorded.path)
        assertEquals("POST", recorded.method)
        assertTrue(recorded.getHeader("X-Signature") != null)
        assertTrue(recorded.getHeader("X-Device-Token") != null)
        assertTrue(recorded.body.readUtf8().contains("\"action\":\"handshake\""))
    }

    @Test
    fun `performHandshake deactivates premium when premium_active is false`() = runTest {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"status":"ok","data":{"premium_active":false}}"""
            )
        )

        val result = BackendClient.performHandshake(context)

        assertTrue(result)
        assertFalse(Prefs.isPremium(context))
    }

    @Test
    fun `performHandshake returns false on non-200 response`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500))

        val result = BackendClient.performHandshake(context)

        assertFalse(result)
    }

    @Test
    fun `performHandshake returns false when status is not ok`() = runTest {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"status":"error"}"""))

        val result = BackendClient.performHandshake(context)

        assertFalse(result)
    }

    @Test
    fun `activatePremium sets premium true on ok response`() = runTest {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true}"""))

        val result = BackendClient.activatePremium(context, "premium_tinyurl_30days")

        assertTrue(result)
        assertTrue(Prefs.isPremium(context))

        val recorded = server.takeRequest()
        assertEquals("/activate_premium.php", recorded.path)
        assertTrue(recorded.body.readUtf8().contains("premium_tinyurl_30days"))
    }

    @Test
    fun `activatePremium does not set premium on ok false response`() = runTest {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":false}"""))

        val result = BackendClient.activatePremium(context, "premium_tinyurl_30days")

        assertFalse(result)
        assertFalse(Prefs.isPremium(context))
    }

    @Test
    fun `deactivatePremium clears premium and resets source on ok response`() = runTest {
        Prefs.setPremium(context, true)
        Prefs.setPremiumSource(context, "inapp")
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"ok":true}"""))

        val result = BackendClient.deactivatePremium(context)

        assertTrue(result)
        assertFalse(Prefs.isPremium(context))
        assertEquals("none", Prefs.getPremiumSource(context))

        val recorded = server.takeRequest()
        assertEquals("/deactivate_premium.php", recorded.path)
    }

    @Test
    fun `deactivatePremium leaves state unchanged on server error`() = runTest {
        Prefs.setPremium(context, true)
        Prefs.setPremiumSource(context, "subscription")
        server.enqueue(MockResponse().setResponseCode(500))

        val result = BackendClient.deactivatePremium(context)

        assertFalse(result)
        assertTrue(Prefs.isPremium(context))
        assertEquals("subscription", Prefs.getPremiumSource(context))
    }
}
