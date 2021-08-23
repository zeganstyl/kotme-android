package com.kotme.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kotme.databinding.LegendBinding

class LegendFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = LegendBinding.inflate(inflater, container, false).apply {
        next.setOnClickListener { findNavController().popBackStack() }
    }.root
}
