package com.snote.domezos.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlin.math.max

object ImageUtils {
    private const val MAX_EDGE = 1600
    private const val JPEG_QUALITY = 80

    /** Loads, downscales and compresses an image. Returns Base64 JPEG, or null on failure. */
    fun prepareForUpload(context: Context, uri: Uri): String? {
        val bitmap = decodeScaledBitmap(context, uri) ?: return null
        val bytes = compressToJpeg(bitmap)
        bitmap.recycle()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    /**
     * Decodes a downscaled bitmap for Compose preview / upload.
     * Photo Picker ("content://media/picker/...") URIs frequently return null from
     * ContentResolver.openInputStream() even though the image is readable, so
     * ImageDecoder (API 28+) / MediaStore.getBitmap (fallback) are used instead of
     * BitmapFactory.decodeStream directly on the URI's stream.
     */
    fun decodeScaledBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val full = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                    val sample = calculateInSampleSize(info.size.width, info.size.height, MAX_EDGE)
                    if (sample > 1) decoder.setTargetSampleSize(sample)
                    decoder.isMutableRequired = true
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
            if (full == null) {
                android.util.Log.e("ImageUtils", "decodeScaledBitmap: decode returned null for $uri")
                return null
            }
            scaleDown(full)
        } catch (e: Exception) {
            android.util.Log.e("ImageUtils", "decodeScaledBitmap: exception for $uri", e)
            null
        }
    }

    private fun calculateInSampleSize(width: Int, height: Int, maxEdge: Int): Int {
        var inSampleSize = 1
        val longestEdge = max(width, height)
        while (longestEdge / (inSampleSize * 2) >= maxEdge) {
            inSampleSize *= 2
        }
        return inSampleSize
    }

    fun bitmapFromBase64(base64: String): Bitmap? {
        return try {
            val cleanBase64 = if (base64.contains(",")) base64.substringAfter(",") else base64
            val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            if (bitmap == null) android.util.Log.e("ImageUtils", "BitmapFactory returned null")
            bitmap
        } catch (e: Exception) {
            android.util.Log.e("ImageUtils", "Error decoding base64", e)
            null
        }
    }

    private fun scaleDown(bitmap: Bitmap): Bitmap {
        val longestEdge = max(bitmap.width, bitmap.height)
        if (longestEdge <= MAX_EDGE) return bitmap
        val scale = MAX_EDGE.toFloat() / longestEdge
        val newWidth = (bitmap.width * scale).toInt()
        val newHeight = (bitmap.height * scale).toInt()
        val scaled = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        if (scaled !== bitmap) bitmap.recycle()
        return scaled
    }

    private fun compressToJpeg(bitmap: Bitmap): ByteArray {
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        return out.toByteArray()
    }
}
