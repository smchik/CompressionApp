package com.example.compressionapp.utils

import android.graphics.Bitmap
import androidx.annotation.IntRange
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class CompressTransformation(
    @IntRange(from = 1, to = 100) private val quality: Int
) : BitmapTransformation() {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        val name = "CompressTransformation$quality"
        messageDigest.update(name.toByteArray(CHARSET))
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val width = toTransform.width
        val height = toTransform.height

        val outputBitmap = pool[width, height, Bitmap.Config.ARGB_8888]
        val result = CompressionHelper.compress(quality, toTransform, outputBitmap)
        return result.bitmap
    }
}
