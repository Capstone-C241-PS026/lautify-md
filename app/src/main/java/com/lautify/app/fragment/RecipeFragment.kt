package com.lautify.app.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import com.lautify.app.adapter.UserAdapter
import com.lautify.app.api.ApiClient
import com.lautify.app.api.response.RecipesResponse
import com.lautify.app.databinding.FragmentRecipeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class   RecipeFragment : Fragment() {

    private val TAG: String = "RecipeFragment"
    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!
    private val list: ArrayList<RecipesResponse> = ArrayList<RecipesResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using View Binding
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        binding.rvData.layoutManager = LinearLayoutManager(context)
        binding.rvData.setHasFixedSize(true)

        // Show loading indicator
        binding.progressBar.visibility = View.VISIBLE
        binding.editTextSearch.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString().trim()
                if (query.isNotEmpty()) {
                    // Clear focus from EditText
                    textView.clearFocus()
                    searchRecipes(query)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        // Fetch and display recipes
        getRecipesList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getRecipesList() {
        ApiClient.instances.getRecipes().enqueue(object : Callback<List<RecipesResponse>> {
            override fun onFailure(call: Call<List<RecipesResponse>>, t: Throwable) {
                printLog(t.toString())
                // Hide loading indicator
                binding.progressBar.visibility = View.GONE
            }

            override fun onResponse(
                call: Call<List<RecipesResponse>>,
                response: Response<List<RecipesResponse>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.let {
                        list.addAll(it)
                        val adapter = UserAdapter(list, requireActivity())
                        binding.rvData.adapter = adapter
                        printLog("Recipes loaded: ${it.size}")
                    }
                }
                // Hide loading indicator
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun searchRecipes(query: String) {
        // Show loading indicator
        binding.progressBar.visibility = View.VISIBLE

        ApiClient.instances.getSearch(query).enqueue(object : Callback<List<RecipesResponse>> {
            override fun onFailure(call: Call<List<RecipesResponse>>, t: Throwable) {
                printLog(t.toString())
                // Hide loading indicator
                binding.progressBar.visibility = View.GONE
            }

            override fun onResponse(
                call: Call<List<RecipesResponse>>,
                response: Response<List<RecipesResponse>>
            ) {
                // Hide loading indicator
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val result = response.body()
                    result?.let {
                        list.clear() // Clear the existing list before adding search results
                        list.addAll(it)
                        val adapter = UserAdapter(list, requireActivity())
                        binding.rvData.adapter = adapter
                        printLog("Search results loaded: ${it.size}")
                    }
                } else {
                    printLog("Failed to get search results")
                }
            }
        })
    }



    private fun printLog(message: String) {
        Log.d(TAG, message)
    }
}
