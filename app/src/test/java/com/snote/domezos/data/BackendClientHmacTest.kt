package com.snote.domezos.data

import org.junit.Assert.assertEquals
import org.junit.Test

class BackendClientHmacTest {

    // Reference vector computed independently via `openssl dgst -sha256 -hmac "change-me"`
    // for message "hello", pinned as a regression check against accidental algorithm changes.
    @Test
    fun `hmacSha256 matches known reference vector`() {
        val signature = BackendClient.hmacSha256("change-me", "hello")
        assertEquals("77bc6d5daa34219e51ed3e4be744c2867365b3d6def704e6c9f7c1c517bac42a", signature)
        assertEquals(64, signature.length)
        assertTrue(signature.matches(Regex("[0-9a-f]{64}")))
    }

    @Test
    fun `hmacSha256 is deterministic for the same key and data`() {
        val a = BackendClient.hmacSha256("secret", "payload")
        val b = BackendClient.hmacSha256("secret", "payload")
        assertEquals(a, b)
    }

    @Test
    fun `hmacSha256 differs when data changes`() {
        val a = BackendClient.hmacSha256("secret", "payload-1")
        val b = BackendClient.hmacSha256("secret", "payload-2")
        assertTrue(a != b)
    }

    @Test
    fun `hmacSha256 differs when key changes`() {
        val a = BackendClient.hmacSha256("secret-a", "payload")
        val b = BackendClient.hmacSha256("secret-b", "payload")
        assertTrue(a != b)
    }

    private fun assertTrue(condition: Boolean) = org.junit.Assert.assertTrue(condition)
}
