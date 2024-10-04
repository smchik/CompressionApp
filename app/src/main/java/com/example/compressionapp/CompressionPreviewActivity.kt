package com.example.compressionapp

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ImageViewTarget
import com.example.compressionapp.databinding.ActivityCompressionPreviewBinding
import com.example.compressionapp.utils.CompressionHelper
import com.example.compressionapp.utils.formatByteSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class CompressionPreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompressionPreviewBinding

    private val imageFile: File by lazy {
        File(intent.getStringExtra(KEY_IMAGE_FILE)!!)
    }

    private var compressionJob: Job? = null
    private var compressedPreviewBitmap: Bitmap? = null
    private var originalPreviewBitmap: Bitmap? = null
    private val originalImageSize: Int
        get() = imageFile.length().toInt()

    private var originalPreviewSize: Int = 0
    private var compressedEstimatedSize: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCompressionPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadImagePreview()

        binding.slider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                compressImagePreview(value.toInt())
            }
        }

        binding.nextButton.setOnClickListener {
            val intent = Intent(this@CompressionPreviewActivity, CompareActivity::class.java)
            intent.putExtra(CompareActivity.KEY_ORIGINAL_IMAGE_FILE, imageFile.path)
            intent.putExtra(CompareActivity.KEY_COMPRESSED_QUALITY, binding.slider.value.toInt())
            intent.putExtra(CompareActivity.KEY_COMPRESSED_SIZE, compressedEstimatedSize)
            startActivity(intent)
        }
    }

    private fun loadImagePreview() {
        Glide.with(this@CompressionPreviewActivity)
            .asBitmap()
            .load(imageFile)
            .into(object : ImageViewTarget<Bitmap>(binding.previewImageView) {
                override fun setResource(resource: Bitmap?) {
                    originalPreviewBitmap = resource?.copy(Bitmap.Config.ARGB_8888, false)
                    view.setImageBitmap(resource)
                    onPreviewReady()
                }
            })
    }

    private fun onPreviewReady() {
        originalPreviewBitmap?.let { bitmap ->
            lifecycleScope.launch {
                originalPreviewSize = withContext(Dispatchers.IO) {
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    val size = stream.size()
                    stream.close()
                    return@withContext size
                }
                compressImagePreview(binding.slider.value.toInt())
            }
        }
    }

    private fun compressImagePreview(quality: Int) {
        originalPreviewBitmap?.let { bitmap ->
            compressionJob?.cancel()
            compressionJob = lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    CompressionHelper.compress(quality, bitmap, compressedPreviewBitmap)
                }
                binding.previewImageView.setImageBitmap(result.bitmap)
                compressedPreviewBitmap = result.bitmap

                val compressedSize =
                    originalImageSize.toFloat() / originalPreviewSize * result.byteSize

                compressedEstimatedSize = compressedSize.toInt()

                binding.infoTextView.text = getString(
                    R.string.label_preview_info,
                    quality,
                    compressedSize.toInt().formatByteSize(),
                    (compressedSize * 100f / originalImageSize).toInt(),
                )
            }
        }
    }

    companion object {
        const val KEY_IMAGE_FILE = "key_image_file"
    }
}
