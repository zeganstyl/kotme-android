package com.kotme.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kotme.R
import com.kotme.databinding.LoginBinding
import android.accounts.AccountManager

class LoginFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LoginBinding.inflate(inflater, container, false).apply {
        val am = AccountManager.get(context)
        val acc = am.accounts.first()

        signUp.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }.root
}