package com.lautify.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lautify.app.databinding.ActivityMainBinding
import com.lautify.app.fragment.CameraFragment
import com.lautify.app.fragment.HomeFragment
import com.lautify.app.fragment.ProfileFragment
import com.lautify.app.fragment.RecipeFragment
import com.lautify.app.fragment.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val targetFragment = intent.getStringExtra("targetFragment")

        if (targetFragment != null) {
            when (targetFragment) {
                "CameraFragment" -> replaceFragment(CameraFragment())
                "RecipeFragment" -> replaceFragment(RecipeFragment())
                // Add more cases if needed
                else -> replaceFragment(HomeFragment())
            }
        } else {
            replaceFragment(HomeFragment())
        }

        // Set up bottom navigation view
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.info -> replaceFragment(SearchFragment())
                R.id.recipe -> replaceFragment(RecipeFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                else -> false
            }
            true
        }

        val cameraButton: FloatingActionButton = findViewById(R.id.btn_camera)
        cameraButton.setOnClickListener {
            replaceFragment(CameraFragment())
        }
    }



    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
