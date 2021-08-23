package com.kotme.fragment

import android.view.View
import android.os.Bundle
import android.text.Spannable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotme.KotmeRepository
import com.kotme.R
import com.kotme.databinding.ExerciseDescriptionBinding
import com.kotme.markdown.Highlighter
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ExerciseDescriptionDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ExerciseDescriptionBinding.inflate(inflater, container, false).also { binding ->
            val viewModel by viewModels<ExerciseDescriptionViewModel>()
            viewModel.exercise.observe(this) {
                binding.title.text = getString(R.string.exercise_number, it?.id?.toString() ?: "")

                binding.storyText.text = it?.storyText
                binding.description.text = it?.exerciseText

                viewModel.highlight(binding.initialCode, it?.initialCode ?: "")
            }

            binding.next.setOnClickListener { dismiss() }
        }.root
}

@HiltViewModel
class ExerciseDescriptionViewModel @Inject constructor(
    val highlighter: Highlighter,
    repo: KotmeRepository
) : ViewModel() {
    val exercise = repo.currentExerciseLiveData(viewModelScope)

    fun highlight(text: Spannable) {
        viewModelScope.launch { highlighter.highlight(text) }
    }

    fun highlight(view: TextView, code: String) {
        viewModelScope.launch { highlighter.highlight(view, code) }
    }
}