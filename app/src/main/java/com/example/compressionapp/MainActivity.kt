package com.example.compressionapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.compressionapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val KEY_IMAGE_FILE_PATH = "key_image_uri"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var imageFile: File? = null
        set(value) {
            field = value
            binding.nextButton.isVisible = value != null
            binding.instructionsTextView.isVisible = value == null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextButton.isVisible = false

        val openDocumentLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
                onImageSelected(result)
            }

        binding.nextButton.setOnClickListener {
            imageFile?.let { openCompareScreen(it) }
        }
        binding.fab.setOnClickListener {
            openDocumentLauncher.launch(arrayOf("image/*"))
        }
    }

    private fun onImageSelected(image: Uri?) {
        lifecycleScope.launch {
            val imageCacheFile = withContext(Dispatchers.IO) {
                try {
                    Glide.with(this@MainActivity)
                        .downloadOnly()
                        .load(image)
                        .submit()
                        .get()
                } catch (e: Exception) {
                    null
                }
            }
            if (imageCacheFile != null) {
                imageFile = imageCacheFile

                loadPreview(imageCacheFile)
                openCompareScreen(imageCacheFile)
            }
        }
    }

    private fun loadPreview(image: File) {
        Glide.with(this)
            .load(image)
            .fitCenter()
            .into(binding.imageView)
    }

    private fun openCompareScreen(image: File) {
        val intent = Intent(this, CompressionPreviewActivity::class.java)
        intent.putExtra(CompressionPreviewActivity.KEY_IMAGE_FILE, image.path)
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        imageFile?.let {
            outState.putString(KEY_IMAGE_FILE_PATH, it.path)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.containsKey(KEY_IMAGE_FILE_PATH)) {
            savedInstanceState.getString(KEY_IMAGE_FILE_PATH)?.let {
                val file = File(it)
                imageFile = file
                loadPreview(file)
            }
        }
    }
}
