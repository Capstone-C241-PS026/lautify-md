package com.lautify.app.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.lautify.app.UserPreferece
import com.lautify.app.adapter.RecipeAdapter
import com.lautify.app.api.ApiClient
import com.lautify.app.api.response.RecipesResponse
import com.lautify.app.databinding.FragmentSavedRecipesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SavedRecipeFragment : Fragment() {

    private val TAG: String = "SavedRecipeFragment"
    private var _binding: FragmentSavedRecipesBinding? = null
    private val binding get() = _binding!!
    private val list: ArrayList<RecipesResponse> = ArrayList<RecipesResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using View Binding
        _binding = FragmentSavedRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        binding.rvData.layoutManager = LinearLayoutManager(context)
        binding.rvData.setHasFixedSize(true)

        // Show loading indicator
        binding.progressBar.visibility = View.VISIBLE


        val preferenceHelper = UserPreferece(requireContext())
        // Fetch and display recipes
        val uid  = preferenceHelper.getUserId()
        getRecipesList(uid.toString())
        Log.d(TAG,uid.toString())
        Log.d(TAG,uid.toString())
    }

    private fun getRecipesList(uid: String) {
        // Initialize the adapter once
        val adapter = RecipeAdapter(ArrayList(), requireActivity(), true)
        binding.rvData.adapter = adapter

        ApiClient.instances.getRecipesSaved(uid).enqueue(object : Callback<List<RecipesResponse>> {
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
                        // Clear the list and update with new data
                        val list = ArrayList<RecipesResponse>(it)
                        adapter.updateRecipes(list)
                        printLog("Recipes loaded: ${it.size}")
                    }
                } else {
                    Log.d(TAG, "Response unsuccessful: ${response.errorBody()?.string()}")
                }
                binding.progressBar.visibility = View.GONE
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
