package com.kotme.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.kotme.KotmeRepository
import com.kotme.R
import com.kotme.databinding.MapBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = MapBinding.inflate(inflater).apply {
        val viewModel by viewModels<MapViewModel>()

        val markers = ArrayList<View>()
        markers.add(exercise1)
        markers.add(exercise2)
        markers.add(exercise3)
        markers.add(exercise4)
        markers.add(exercise5)
        markers.add(exercise6)
        markers.add(exercise7)
        markers.add(exercise8)
        markers.add(exercise9)
        markers.add(exercise10)

        markers.forEachIndexed { index, view ->
            view.setOnClickListener {
                findNavController().navigate(MapFragmentDirections.toLesson(index + 1))
            }
        }

        //viewModel.viewModelScope.launch { viewModel.repo.getUpdates(0) }

        viewModel.currentProgressExercise.observe(viewLifecycleOwner) { exercise ->
            markers.forEach { it.background = null }
            exercise?.also {
                for (i in 0 until it.number) {
                    markers[i].also { marker ->
                        marker.setBackgroundResource(R.drawable.map_marker_completed)
                        marker.animate()
                            .alpha(1f)
                            .setDuration(500L)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    marker.visibility = View.VISIBLE
                                }
                            })
                    }
                }
                markers[it.number - 1].setBackgroundResource(R.drawable.map_marker_current)
            }
        }

        CharacterDialog().show(childFragmentManager, null)
    }.root
}

@HiltViewModel
class MapViewModel @Inject constructor(val repo: KotmeRepository) : ViewModel() {
    val currentProgressExercise = repo.currentProgressExercise()
}