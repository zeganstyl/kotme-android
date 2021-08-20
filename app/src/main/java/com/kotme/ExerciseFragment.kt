package com.kotme

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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.kotme.data.CodeCheckResultStatus
import com.kotme.databinding.ExerciseBinding
import com.kotme.markdown.Highlighter
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
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

        code.addTextChangedListener {
            if (it != null) {
                viewModel.highlight(it)
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
                    ResultsDialog().show()
                }
            }
        }

        viewModel.exercise.observe(viewLifecycleOwner) {
            results.text = it?.resultStatus?.toString()
            viewModel.highlight(code, it?.initialCode ?: "")
        }
    }.root
}

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    val highlighter: Highlighter,
    val repo: KotmeRepository
) : ViewModel() {
    val exercise = repo.currentExerciseLiveData(viewModelScope)

    fun saveCode(code: String) {
        exercise.value?.also {
            viewModelScope.launch { repo.saveCode(it.id, code) }
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