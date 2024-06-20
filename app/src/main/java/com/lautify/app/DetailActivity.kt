package com.lautify.app.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.lautify.app.MainActivity
import com.lautify.app.UserPreferece
import com.lautify.app.api.ApiClient
import com.lautify.app.api.response.DetailResponse
import com.lautify.app.api.response.SimpleResponse
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

        binding.btnRecipe.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("targetFragment", "RecipeFragment")
            }
            startActivity(intent)
        }

        val preferenceHelper = UserPreferece(this.applicationContext)
        val uid: String = preferenceHelper.getUserId().toString()

        Log.d("UserId", uid)
        Log.d("recipeId", rid.toString())

        binding.btnSaveRecipe.setOnClickListener {
            rid?.let { saveRecipeById(uid, it) }
        }
    }

    private fun saveRecipeById(uid: String, rid: String) {
        ApiClient.instances.saveRecipe(uid, rid).enqueue(object : Callback<SimpleResponse> {
            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Toast.makeText(this@DetailActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DetailActivity, "Failed to save recipe", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                Toast.makeText(this@DetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
            builder.append("${index + 1}. $instruction\n\n")
        }
        return builder.toString()
    }

    private fun buildIngredientsText(ingredients: List<String>): String {
        val builder = StringBuilder()
        for (ingredient in ingredients) {
            builder.append("$ingredient\n\n")
        }
        return builder.toString()
    }
}
