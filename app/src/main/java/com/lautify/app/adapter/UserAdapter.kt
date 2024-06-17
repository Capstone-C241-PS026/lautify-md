package com.lautify.app.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lautify.app.api.response.RecipesResponse
import com.lautify.app.databinding.RecipeItemBinding
import com.lautify.app.ui.DetailActivity

class UserAdapter(
    private val listRecipe: ArrayList<RecipesResponse>,
    private val activity: FragmentActivity
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: RecipeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipesResponse: RecipesResponse) {
            with(binding) {
                Glide.with(itemView.context).load(recipesResponse.image).into(cardImage)
                title.text = recipesResponse.title
                itemView.setOnClickListener {
                    val intent = Intent(activity, DetailActivity::class.java).apply {
                        putExtra("RID", recipesResponse.rid.toString())
                        putExtra("TITLE", recipesResponse.title)
                        putExtra("RECIPE_PHOTO_URL", recipesResponse.image)

                    }
                    activity.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listRecipe.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listRecipe[position])
    }
}
