package com.kotme

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotme.databinding.ResultsBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ResultsDialog : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ResultsBinding.inflate(inflater, container, false).apply {
        next.setOnClickListener { hide() }

        val viewModel by viewModels<ResultsViewModel>()

        message.text = viewModel.exercise.value?.resultMessage
        console.text = viewModel.exercise.value?.resultConsole

//        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
//        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }.root
}

@HiltViewModel
class ResultsViewModel @Inject constructor(repo: KotmeRepository) : ViewModel() {
    val exercise = repo.currentExerciseLiveData(viewModelScope)
}