package com.thelemistix.kotme

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView

class SignUpDialog(val mainActivity: MainActivity): Dialog(mainActivity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.sign_up)

        val login = findViewById<TextView>(R.id.login)!!
        val password = findViewById<TextView>(R.id.password)!!
        val retypePassword = findViewById<TextView>(R.id.retypePassword)!!

        if (password.text == retypePassword.text) {
            mainActivity.client.signUp(login.text.toString(), password.text.toString())
        }

//        findViewById<View>(R.id.signIn).setOnClickListener {
//            if (mainActivity.client.signIn(login.text.toString(), password.text.toString())) {
//                setMessage("")
//                hide()
//            } else {
//                setMessage("Не верный логин или пароль", Color.RED)
//            }
//        }
//
//        messageView = findViewById(R.id.message)
//        setMessage(message, messageColor)

        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}