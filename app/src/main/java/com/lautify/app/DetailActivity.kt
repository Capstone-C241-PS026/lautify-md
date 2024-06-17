package com.lautify.app.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.lautify.app.api.ApiClient
import com.lautify.app.api.response.DetailResponse
import com.lautify.app.databinding.ActivityDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rid = intent.getStringExtra("RID")
        val title = intent.getStringExtra("TITLE")
        val recipePhotoUrl = intent.getStringExtra("RECIPE_PHOTO_URL")

        binding.titleRecipe.text = title
        Glide.with(this).load(recipePhotoUrl).into(binding.cardImage)

        // Show ProgressBar
        binding.progressBar.visibility = View.VISIBLE

        // Fetch detailed recipe information
        rid?.let { fetchRecipeDetail(it) }
    }

    private fun fetchRecipeDetail(rid: String) {
        ApiClient.instances.getRecipesDetails(rid).enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                // Hide ProgressBar
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    response.body()?.let {
                        displayRecipeDetail(it)
                    }
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                // Hide ProgressBar on failure
                binding.progressBar.visibility = View.GONE
                // Handle failure
            }
        })
    }

    private fun displayRecipeDetail(detail: DetailResponse) {
        binding.instructions.text = buildInstructionsText(detail.instructions)
        binding.ingredients.text = buildIngredientsText(detail.ingredients)
    }

    private fun buildInstructionsText(instructions: List<String>): String {
        val builder = StringBuilder()
        for ((index, instruction) in instructions.withIndex()) {
            builder.append("${index + 1}. $instruction\n")
        }
        return builder.toString()
    }

    private fun buildIngredientsText(ingredients: List<String>): String {
        val builder = StringBuilder()
        for ((index, ingredient) in ingredients.withIndex()) {
            builder.append("${index + 1}. $ingredient\n")
        }
        return builder.toString()
    }
}
