package com.kotme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.kotme.databinding.MapBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : Fragment() {
    val viewModel by viewModels<MapViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = MapBinding.inflate(inflater).apply {
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
            message.text = exercise?.characterMessage ?: ""
            exercise?.also {
                for (i in 0 until it.number) {
                    markers[i].setBackgroundResource(R.drawable.map_marker_completed)
                }
                markers[it.number - 1].setBackgroundResource(R.drawable.map_marker_current)
            }
        }

        next.setOnClickListener {
            viewModel.userProgress.value?.also {
                viewModel.setExercise(it + 1)
                findNavController().navigate(MapFragmentDirections.toLesson(it + 1))
            }
        }
    }.root
}

@HiltViewModel
class MapViewModel @Inject constructor(val repo: KotmeRepository) : ViewModel() {
    val userProgress = repo.userProgress().asLiveData()

    val currentProgressExercise = repo.currentProgressExerciseLiveData(viewModelScope)

    fun setExercise(number: Int) {
        viewModelScope.launch { repo.setCurrentExercise(number) }
    }
}