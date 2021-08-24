package com.kotme.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.kotme.KotmeRepository
import com.kotme.R
import com.kotme.databinding.LessonBinding
import com.kotme.markdown.MyGrammarLocator
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import io.noties.markwon.Markwon
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import javax.inject.Inject

@AndroidEntryPoint
class LessonFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = LessonBinding.inflate(inflater).apply {
        val viewModel by viewModels<LessonViewModel>()

        next.setOnClickListener {
            findNavController().navigate(R.id.exerciseFragment)
        }

        val prism4j = Prism4j(MyGrammarLocator())
        val markdown = Markwon.builder(context!!)
            .usePlugin(SyntaxHighlightPlugin.create(prism4j, Prism4jThemeDarkula.create()))
            .build()

        viewModel.exercise.observe(viewLifecycleOwner) {
            markdown.setMarkdown(lessonText, it?.lessonText ?: "")
        }
    }.root
}

@HiltViewModel
class LessonViewModel @Inject constructor(repo: KotmeRepository) : ViewModel() {
    val exercise = repo.currentExerciseLiveData(viewModelScope)
}