package com.lautify.app.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lautify.app.api.response.RecipesResponse
import com.lautify.app.databinding.RecipeItemBinding
import com.lautify.app.databinding.EmptyItemBinding
import com.lautify.app.ui.DetailActivity
import com.lautify.app.ui.DetailSavedActivity

class RecipeAdapter(
    private val listRecipe: ArrayList<RecipesResponse>,
    private val activity: FragmentActivity,
    private val isSaved: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 1
    private val VIEW_TYPE_EMPTY = 0

    inner class ViewHolder(private val binding: RecipeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipesResponse: RecipesResponse) {
            with(binding) {
                Glide.with(itemView.context).load(recipesResponse.image).into(cardImage)
                title.text = recipesResponse.title
                readyInMenute.text = recipesResponse.readyInMinutes.toString()
                itemView.setOnClickListener {
                    val intent = if (isSaved) {
                        Intent(activity, DetailSavedActivity::class.java)
                    } else {
                        Intent(activity, DetailActivity::class.java)
                    }.apply {
                        putExtra("RID", recipesResponse.rid.toString())
                        putExtra("TITLE", recipesResponse.title)
                        putExtra("READY_IN_MINUTES", recipesResponse.readyInMinutes.toString())
                        putExtra("RECIPE_PHOTO_URL", recipesResponse.image)
                    }
                    activity.startActivity(intent)
                }
            }
        }
    }

    inner class EmptyViewHolder(private val binding: EmptyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.emptyText.text = "Tidak ada recipe"
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listRecipe.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_EMPTY) {
            val binding = EmptyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            EmptyViewHolder(binding)
        } else {
            val binding = RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return if (listRecipe.isEmpty()) 1 else listRecipe.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(listRecipe[position])
        } else if (holder is EmptyViewHolder) {
            holder.bind()
        }
    }

    fun updateRecipes(newRecipes: List<RecipesResponse>) {
        listRecipe.clear()
        listRecipe.addAll(newRecipes)
        notifyDataSetChanged()
    }
}
