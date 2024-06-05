package com.lautify.app.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.lautify.app.LoginActivity
import com.lautify.app.R


class ProfileFragment : Fragment() {

    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        btnLogout = view.findViewById(R.id.btn_logout)

        btnLogout.setOnClickListener {

            val intent = Intent(requireActivity(), LoginActivity::class.java) // Use requireActivity() for context
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        return view
    }
}
