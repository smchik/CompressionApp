package com.example.compressionapp

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.compressionapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextButton.isVisible = false

        val openDocumentLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
                Glide.with(this)
                    .load(result)
                    .fitCenter()
                    .into(binding.imageView)

                binding.nextButton.isVisible = true
                binding.nextButton.setOnClickListener {
                    // open the next screen
                }
            }

        binding.fab.setOnClickListener {
            // allow users to choose any image type, later we'll treat it as jpeg
            openDocumentLauncher.launch(arrayOf("image/*"))
        }
    }
}
