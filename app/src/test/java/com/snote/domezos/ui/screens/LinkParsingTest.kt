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
    fun `com param wins over link param and is not treated as a link`() {
        val url = "https://domezos-ware.org/msges/view.php?com=abc12&pass=secret"
        val parsed = parseAliasFromUri(Uri.parse(url), url)

        assertEquals("abc12", parsed.rawAlias)
        assertEquals("secret", parsed.pass)
        assertFalse(parsed.isLink)
    }

    @Test
    fun `link param on snote fun is treated as a link`() {
        val url = "https://snote.fun?link=xyz99"
        val parsed = parseAliasFromUri(Uri.parse(url), url)

        assertEquals("xyz99", parsed.rawAlias)
        assertEquals("", parsed.pass)
        assertTrue(parsed.isLink)
    }

    @Test
    fun `snote fun host without explicit link param still resolves as a link via lastPathSegment`() {
        val url = "https://snote.fun/abcde"
        val parsed = parseAliasFromUri(Uri.parse(url), url)

        assertEquals("abcde", parsed.rawAlias)
        assertTrue(parsed.isLink)
    }

    @Test
    fun `missing com, link params and path segment yields empty alias`() {
        val url = "https://domezos-ware.org"
        val parsed = parseAliasFromUri(Uri.parse(url), url)

        assertEquals("", parsed.rawAlias)
    }

    @Test
    fun `missing com and link params falls back to the last path segment`() {
        val url = "https://domezos-ware.org/msges/view.php"
        val parsed = parseAliasFromUri(Uri.parse(url), url)

        assertEquals("view.php", parsed.rawAlias)
    }

    @Test
    fun `pass defaults to empty string when absent`() {
        val url = "https://domezos-ware.org/msges/view.php?com=abc12"
        val parsed = parseAliasFromUri(Uri.parse(url), url)

        assertEquals("", parsed.pass)
    }

    @Test
    fun `formatGeneratedAlias truncates com alias with ellipsis`() {
        val result = "https://domezos-ware.org/msges/view.php?com=abcdefghijklmnopqrstuvwxyz&pass=x"
        assertEquals("abcdefghijkl...", formatGeneratedAlias(result))
    }

    @Test
    fun `formatGeneratedAlias truncates link alias to 8 chars`() {
        val result = "https://snote.fun?link=abcdefghij"
        assertEquals("abcdefgh", formatGeneratedAlias(result))
    }

    @Test
    fun `formatGeneratedAlias falls back when neither com nor link present`() {
        val result = "https://domezos-ware.org/msges/view.php"
        assertEquals("Link Ready", formatGeneratedAlias(result))
    }
}
