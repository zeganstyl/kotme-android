package com.kotme.fragment

import android.os.Bundle
import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.kotme.KotmeRepository
import com.kotme.R
import com.kotme.common.CodeCheckResultStatus
import com.kotme.databinding.ExerciseBinding
import com.kotme.markdown.Highlighter
import com.kotme.common.show
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ExerciseFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ExerciseBinding.inflate(inflater).apply {
        val viewModel by viewModels<ExerciseViewModel>()

        val codeFlow = MutableStateFlow("")
        code.addTextChangedListener {
            if (it != null) {
                viewModel.highlight(it)
                codeFlow.value = it.toString()
            }
        }
        viewModel.viewModelScope.launch {
            codeFlow.debounce(1000L).collect {
                viewModel.saveCode(it)
            }
        }

        description.setOnClickListener {
            ExerciseDescriptionDialog().show()
        }

        check.setOnClickListener { viewModel.checkCode() }

        results.setOnClickListener {
            viewModel.exercise.value?.also {
                if (it.resultStatus == CodeCheckResultStatus.TestsSuccess) {
                    CongratulationsDialog().show()
                } else if (it.resultStatus != CodeCheckResultStatus.NoStatus) {
                    findNavController().navigate(R.id.resultsFragment)
                }
            }
        }

        viewModel.exercise.observe(viewLifecycleOwner) {
            if (it != null) {
                results.text = it.resultStatus.toString()
                if (it.userCode.isNotEmpty()) {
                    if (it.userCode != code.text.toString()) {
                        viewModel.highlight(code, it.userCode)
                    }
                } else {
                    viewModel.highlight(code, it.initialCode)
                }
            }
        }
    }.root
}

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    val highlighter: Highlighter,
    val repo: KotmeRepository
) : ViewModel() {
    val exercise = repo.currentExerciseLiveData(viewModelScope)

    suspend fun saveCode(code: String) {
        exercise.value?.also {
            repo.saveCode(it.id, code)
        }
    }

    fun checkCode() {
        exercise.value?.also {
            viewModelScope.launch { repo.checkCode(it) }
        }
    }

    fun highlight(text: Spannable) {
        viewModelScope.launch { highlighter.highlight(text) }
    }

    fun highlight(view: TextView, code: String) {
        viewModelScope.launch { highlighter.highlight(view, code) }
    }
}