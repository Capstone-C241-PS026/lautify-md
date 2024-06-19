package com.lautify.app.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.lautify.app.adapter.UserAdapter
import com.lautify.app.api.ApiClient
import com.lautify.app.api.response.RecipesResponse
import com.lautify.app.databinding.FragmentRecipeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeFragment : Fragment() {

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

        // Set the click listener for the search button
        binding.btnSearch.setOnClickListener {
            val query = binding.editTextSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                // Clear focus from EditText
                binding.editTextSearch.clearFocus()
                searchRecipes(query)
                Log.d("search", "query: $query")
            } else {
                Log.d("search", "query empty")
            }
        }

        // Fetch and display recipes
        getRecipesList()
    }

    private fun getRecipesList() {
        ApiClient.instances.getRecipes().enqueue(object : Callback<List<RecipesResponse>> {
            override fun onFailure(call: Call<List<RecipesResponse>>, t: Throwable) {
                printLog(t.toString())
                // Check if binding is still valid
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onResponse(
                call: Call<List<RecipesResponse>>,
                response: Response<List<RecipesResponse>>
            ) {
                if (_binding == null) return

                if (response.isSuccessful) {
                    val result = response.body()
                    result?.let {
                        list.addAll(it)
                        val adapter = UserAdapter(list, requireActivity())
                        binding.rvData.adapter = adapter
                        printLog("Recipes loaded: ${it.size}")
                    }
                } else {
                    Log.d(TAG, "Response unsuccessful: ${response.errorBody()?.string()}")
                }
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun searchRecipes(query: String) {
        binding.progressBar.visibility = View.VISIBLE

        ApiClient.instances.getSearch(query).enqueue(object : Callback<List<RecipesResponse>> {
            override fun onFailure(call: Call<List<RecipesResponse>>, t: Throwable) {
                printLog(t.toString())
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onResponse(
                call: Call<List<RecipesResponse>>,
                response: Response<List<RecipesResponse>>
            ) {
                if (_binding == null) return

                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val result = response.body()
                    result?.let {
                        list.clear()
                        list.addAll(it)
                        val adapter = UserAdapter(list, requireActivity())
                        binding.rvData.adapter = adapter
                        printLog("Search results loaded: ${it.size}")
                    }
                } else {
                    Log.d(TAG, "Failed to get search results: ${response.errorBody()?.string()}")
                }
            }
        })
    }


    private fun printLog(message: String) {
        Log.d(TAG, message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
