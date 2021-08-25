package com.kotme.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kotme.R
import com.kotme.databinding.LoginBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotme.KotmeApi
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.features.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LoginBinding.inflate(inflater, container, false).apply {
        val viewModel by viewModels<LoginViewModel>()

        signUp.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }

        signIn.setOnClickListener {
            viewModel.tryLogin(login.text.toString(), password.text.toString())
        }

        viewModel.isLoggedIn.observe(viewLifecycleOwner) {
            println(it)
            message.text = if (it == false) "Incorrect login or password" else ""
        }
    }.root
}

@HiltViewModel
class LoginViewModel @Inject constructor(private val api: KotmeApi): ViewModel() {
    val isLoggedIn = MutableLiveData<Boolean?>()

    fun tryLogin(name: String, pass: String) {
        viewModelScope.launch {
            try {
                isLoggedIn.value = api.tryLogin(name, pass)
            } catch (ex: ClientRequestException) {
                ex.response
            }
        }
    }
}