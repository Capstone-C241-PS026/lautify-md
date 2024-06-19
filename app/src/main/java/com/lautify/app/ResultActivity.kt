package com.lautify.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.lautify.app.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra("imageUrl")
        val freshness = intent.getStringExtra("freshness")

        Glide.with(this)
            .load(imageUrl)
            .into(binding.resultImage)

        binding.freshnessText.text = freshness

        binding.btnRecipe.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("targetFragment", "CameraFragment")
            }
            startActivity(intent)
        }

        binding.gotRecipe.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("targetFragment", "RecipeFragment")
            }
            startActivity(intent)
        }
    }
}
