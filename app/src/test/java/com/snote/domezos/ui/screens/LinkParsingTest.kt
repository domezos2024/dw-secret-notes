package com.snote.domezos.ui.screens

import android.net.Uri
import com.snote.domezos.data.Backend
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Robolectric ships shadows only up to SDK 34 at this dependency version; pin to it since
// this project's compileSdk/targetSdk (37) outruns Robolectric's supported range.
@Config(sdk = [34])
@RunWith(RobolectricTestRunner::class)
class LinkParsingTest {

    @Test
    fun `com param wins over link param`() {
        val parsed = parseAliasFromUri(Uri.parse("${Backend.BASE_URL}/msges/view.php?com=abc12&pass=secret"))
        assertEquals("abc12", parsed.rawAlias)
        assertEquals("secret", parsed.pass)
    }

    @Test
    fun `missing com param and path segment yields empty alias`() {
        val parsed = parseAliasFromUri(Uri.parse(Backend.BASE_URL))
        assertEquals("", parsed.rawAlias)
    }

    @Test
    fun `missing com param falls back to the last path segment`() {
        val parsed = parseAliasFromUri(Uri.parse("${Backend.BASE_URL}/msges/view.php"))
        assertEquals("view.php", parsed.rawAlias)
    }

    @Test
    fun `pass defaults to empty string when absent`() {
        val parsed = parseAliasFromUri(Uri.parse("${Backend.BASE_URL}/msges/view.php?com=abc12"))
        assertEquals("", parsed.pass)
    }

    @Test
    fun `formatGeneratedAlias truncates com alias with ellipsis`() {
        val result = "${Backend.BASE_URL}/msges/view.php?com=abcdefghijklmnopqrstuvwxyz&pass=x"
        assertEquals("abcdefghijkl...", formatGeneratedAlias(result))
    }

    @Test
    fun `formatGeneratedAlias falls back when com not present`() {
        assertEquals("Link Ready", formatGeneratedAlias("${Backend.BASE_URL}/msges/view.php"))
    }
}
