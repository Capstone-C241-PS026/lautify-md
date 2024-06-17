package com.lautify.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.lautify.app.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storyId = arguments?.getString("RID")
        val storyTitle = arguments?.getString("TITLE")
        val storyPhotoUrl = arguments?.getString("RECIPE_PHOTO_URL")

        binding.title.text = storyTitle
        Glide.with(this).load(storyPhotoUrl).into(binding.cardImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
