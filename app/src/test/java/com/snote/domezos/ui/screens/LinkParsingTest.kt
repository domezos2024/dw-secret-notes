package com.snote.domezos.ui.screens

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
        val url = "https://domezos-ware.org/msges/view.php?com=abc12&pass=secret"
        val parsed = parseAliasFromUri(Uri.parse(url))

        assertEquals("abc12", parsed.rawAlias)
        assertEquals("secret", parsed.pass)
    }

    @Test
    fun `missing com param and path segment yields empty alias`() {
        val url = "https://domezos-ware.org"
        val parsed = parseAliasFromUri(Uri.parse(url))

        assertEquals("", parsed.rawAlias)
    }

    @Test
    fun `missing com param falls back to the last path segment`() {
        val url = "https://domezos-ware.org/msges/view.php"
        val parsed = parseAliasFromUri(Uri.parse(url))

        assertEquals("view.php", parsed.rawAlias)
    }

    @Test
    fun `pass defaults to empty string when absent`() {
        val url = "https://domezos-ware.org/msges/view.php?com=abc12"
        val parsed = parseAliasFromUri(Uri.parse(url))

        assertEquals("", parsed.pass)
    }

    @Test
    fun `formatGeneratedAlias truncates com alias with ellipsis`() {
        val result = "https://domezos-ware.org/msges/view.php?com=abcdefghijklmnopqrstuvwxyz&pass=x"
        assertEquals("abcdefghijkl...", formatGeneratedAlias(result))
    }

    @Test
    fun `formatGeneratedAlias falls back when com not present`() {
        val result = "https://domezos-ware.org/msges/view.php"
        assertEquals("Link Ready", formatGeneratedAlias(result))
    }
}
