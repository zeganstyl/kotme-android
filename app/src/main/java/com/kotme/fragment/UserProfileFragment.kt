package com.kotme.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kotme.KotmeRepository
import com.kotme.databinding.UserProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = UserProfileBinding.inflate(inflater, container, false).apply {
        val viewModel by viewModels<UserProfileViewModel>()

        nameSwitcher.setOnClickListener { nameSwitcher.showNext() }

        viewModel.user.observe(viewLifecycleOwner) {
            nameView.text = it?.name
            name.setText(it?.name)
        }
    }.root
}

@HiltViewModel
class UserProfileViewModel @Inject constructor(val repo: KotmeRepository): ViewModel() {
    val user = repo.userDao.getFlow().asLiveData()
}