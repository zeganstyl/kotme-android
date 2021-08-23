package com.kotme.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.kotme.KotmeRepository
import com.kotme.databinding.ResultsBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ResultsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ResultsBinding.inflate(inflater, container, false).apply {
        val viewModel by viewModels<ResultsViewModel>()

        viewModel.exercise.observe(viewLifecycleOwner) {
            message.text = it?.resultMessage
            console.text = it?.resultConsole
        }

        next.setOnClickListener {
            findNavController().popBackStack()
        }
    }.root
}

@HiltViewModel
class ResultsViewModel @Inject constructor(repo: KotmeRepository) : ViewModel() {
    val exercise = repo.currentExerciseLiveData(viewModelScope)
}