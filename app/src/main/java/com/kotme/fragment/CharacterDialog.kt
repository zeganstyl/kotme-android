package com.kotme.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotme.KotmeRepository
import com.kotme.databinding.CharacterDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class CharacterDialog: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = CharacterDialogBinding.inflate(inflater, container, false).apply {
        val viewModel by viewModels<CharacterDialogViewModel>()

        viewModel.currentProgressExercise.observe(viewLifecycleOwner) {
            message.text = it?.characterMessage
        }

        next.setOnClickListener {
            dialog?.hide()
        }
    }.root
}

@HiltViewModel
class CharacterDialogViewModel @Inject constructor(val repo: KotmeRepository) : ViewModel() {
    val currentProgressExercise = repo.currentProgressExerciseLiveData(viewModelScope)
}