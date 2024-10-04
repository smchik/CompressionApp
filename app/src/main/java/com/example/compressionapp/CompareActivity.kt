package com.example.compressionapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.compressionapp.databinding.ActivityCompareBinding
import com.example.compressionapp.utils.CompressTransformation
import com.example.compressionapp.utils.formatByteSize
import java.io.File

class CompareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompareBinding

    private val originalImageFile: File? by lazy {
        intent.getStringExtra(KEY_ORIGINAL_IMAGE_FILE)?.let { File(it) }
    }

    private val compressedQuality: Int by lazy {
        intent.getIntExtra(KEY_COMPRESSED_QUALITY, 90)
    }

    private val compressedSize: Int by lazy {
        intent.getIntExtra(KEY_COMPRESSED_SIZE, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCompareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load(originalImageFile)
            .into(binding.originalImageView)

        Glide.with(this)
            .load(originalImageFile)
            .transform(CompressTransformation(compressedQuality))
            .into(binding.compressedImageView)

        binding.originalImageSizeTextView.text = getString(
            R.string.label_original_image_size,
            originalImageFile?.length()?.formatByteSize(),
        )

        binding.compressedImageSizeTextView.text = getString(
            R.string.label_compressed_image_size,
            compressedSize.formatByteSize(),
        )
    }

    companion object {
        const val KEY_ORIGINAL_IMAGE_FILE = "key_original_image_file"
        const val KEY_COMPRESSED_QUALITY = "key_compresses_quality"
        const val KEY_COMPRESSED_SIZE = "key_compressed_size"
    }
}
