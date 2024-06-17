package com.lautify.app

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val imageView = findViewById<ImageView>(R.id.result_image)
        val freshnessTextView = findViewById<TextView>(R.id.freshness_text)

        val imageUrl = intent.getStringExtra("imageUrl")
        val freshness = intent.getStringExtra("freshness")

        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        freshnessTextView.text = freshness
    }
}
