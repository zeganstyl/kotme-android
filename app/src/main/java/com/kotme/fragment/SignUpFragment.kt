package com.kotme.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import com.kotme.R
import com.kotme.KotmeApi
import com.kotme.databinding.SignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.http.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = SignUpBinding.inflate(inflater, container, false).apply {
        val viewModel by viewModels<SignUpViewModel>()

        viewModel.status.observe(viewLifecycleOwner) {
            if (it == "OK") {
                findNavController().navigate(R.id.mapFragment)
            } else {
                errorMessage.text = it
            }
        }

        accept.setOnClickListener {
            if (password.text.toString() == retypePassword.text.toString()) {
                viewModel.signUp(
                    name.text.toString(),
                    login.text.toString(),
                    password.text.toString()
                )
            }
        }

        back.setOnClickListener {
            findNavController().popBackStack()
        }
    }.root
}

@HiltViewModel
class SignUpViewModel @Inject constructor(val api: KotmeApi): ViewModel() {
    val status = MutableLiveData<String>()

    fun signUp(name: String, login: String, password: String) {
        viewModelScope.launch {
            try {
                val request = api.signUp(name, login, password)
                when (request.call.response.status) {
                    HttpStatusCode.BadRequest -> {
                        status.value = request.call.response.content.readUTF8Line(256) ?: ""
                    }
                    HttpStatusCode.OK -> {
                        status.value = "OK"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}