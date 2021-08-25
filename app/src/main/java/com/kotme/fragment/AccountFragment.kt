package com.kotme.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.kotme.KotmeRepository
import com.kotme.R
import com.kotme.databinding.AccountBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = AccountBinding.inflate(inflater, container, false).apply {
        val viewModel by viewModels<AccountViewModel>()

        nameSwitcher.setOnClickListener { nameSwitcher.showNext() }

        viewModel.user.observe(viewLifecycleOwner) {
            nameView.text = it?.name
            name.setText(it?.name)
        }

        login.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }.root
}

@HiltViewModel
class AccountViewModel @Inject constructor(val repo: KotmeRepository): ViewModel() {
    val user = repo.userDao.getFlow().asLiveData()
}