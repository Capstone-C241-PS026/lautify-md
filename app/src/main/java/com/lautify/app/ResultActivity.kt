package com.lautify.app.ui

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lautify.app.R
import com.lautify.app.databinding.ActivityResultBinding
import com.lautify.app.helper.ImageClassifierHelper

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        val imageUri = imageUriString?.let { Uri.parse(it) }

        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.resultImage.setImageURI(it)
            classifyImage(it)
        } ?: run {
            showToast(getString(R.string.empty_image_warning))
        }
    }

    private fun classifyImage(imageUri: Uri) {
        val classifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    showToast(error)
                }

                override fun onResults(result: String) {
                    binding.resultText.text = result
                }
            }
        )
        // Convert URI to Bitmap
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        classifierHelper.classify(bitmap)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}
