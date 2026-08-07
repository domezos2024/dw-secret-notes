package com.snote.domezos.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ImageUtilsSamplingTest {

    private fun sampleSize(width: Int, height: Int, maxEdge: Int = 1600) =
        ImageUtils.calculateInSampleSize(width, height, maxEdge)

    @Test
    fun `image already within max edge needs no downsampling`() {
        assertEquals(1, sampleSize(800, 600))
    }

    @Test
    fun `image exactly at max edge needs no downsampling`() {
        assertEquals(1, sampleSize(1600, 1200))
    }

    @Test
    fun `just below the halving boundary needs no downsampling`() {
        // 3199 / 2 = 1599 (integer division), which is just under maxEdge, so no halving occurs.
        assertEquals(1, sampleSize(3199, 1000))
    }

    @Test
    fun `at the halving boundary is downsampled once`() {
        // 3200 / 2 = 1600 >= maxEdge, so one halving occurs; the next halving (3200/4=800) doesn't.
        assertEquals(2, sampleSize(3200, 1000))
    }

    @Test
    fun `very large image is downsampled by successive powers of two`() {
        // 6400/2=3200 and 6400/4=1600 both satisfy the loop condition, 6400/8=800 does not,
        // so inSampleSize stops at 4.
        val result = sampleSize(6400, 4800)
        assertEquals(4, result)
    }

    @Test
    fun `uses the longer edge to decide sampling`() {
        // Width is huge, height is small: longest edge (width) should drive the calculation.
        val wideResult = sampleSize(width = 6400, height = 100)
        val tallResult = sampleSize(width = 100, height = 6400)
        assertEquals(wideResult, tallResult)
    }

    @Test
    fun `zero dimensions do not crash and return the minimum sample size`() {
        assertEquals(1, sampleSize(0, 0))
    }
}
