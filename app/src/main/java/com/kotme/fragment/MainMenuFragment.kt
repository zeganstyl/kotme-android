package com.kotme.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kotme.R
import com.kotme.databinding.MainMenuBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainMenuFragment: Fragment(R.layout.main_menu) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = MainMenuBinding.inflate(inflater).apply {
        play.setOnClickListener {
            findNavController().navigate(R.id.mapFragment)

//            if (mainActivity.db.progress > 0) {
//                mainActivity.map.setCurrentExercise()
//                findNavController().navigate(R.id.mapFragment)
//            } else {
//                findNavController().navigate(R.id.legendFragment)
//            }
        }

        achievements.setOnClickListener { findNavController().navigate(R.id.achievementsFragment) }
        legend.setOnClickListener { findNavController().navigate(R.id.legendFragment) }
    }.root
}