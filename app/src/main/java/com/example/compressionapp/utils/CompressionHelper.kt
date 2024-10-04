package com.example.compressionapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream


object CompressionHelper {

    fun compress(quality: Int, toTransform: Bitmap, outputBitmap: Bitmap?): CompressionResult {

        val stream = ByteArrayOutputStream()
        toTransform.compress(Bitmap.CompressFormat.JPEG, quality, stream)

        val options = if (outputBitmap != null) {
            BitmapFactory.Options().apply {
                inMutable = true
                inBitmap = outputBitmap
            }
        } else {
            null
        }

        val compressedByteArray = stream.toByteArray()
        val compressedBitmap = BitmapFactory.decodeByteArray(
            compressedByteArray,
            0,
            compressedByteArray.size,
            options
        )
        stream.close()
        return CompressionResult(compressedBitmap, compressedByteArray.size)
    }
}


class CompressionResult(
    val bitmap: Bitmap,
    val byteSize: Int,
)
