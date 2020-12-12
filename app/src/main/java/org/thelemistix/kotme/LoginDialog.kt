package org.thelemistix.kotme

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.TextView

class LoginDialog(val mainActivity: MainActivity): Dialog(mainActivity) {
    private var message: String = ""
    private var messageColor: Int = Color.WHITE
    private var messageView: TextView? = null

    var loginText: String = ""
        set(value) {
            field = value
            findViewById<TextView>(R.id.login)?.text = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.login)

        val login = findViewById<TextView>(R.id.login)!!
        val password = findViewById<TextView>(R.id.password)!!
        val remember = findViewById<CheckBox>(R.id.remember)!!

        login.text = loginText

        findViewById<View>(R.id.signIn).setOnClickListener {
            val error = mainActivity.client.signIn(login.text.toString(), password.text.toString(), remember.isChecked)
            if (error.isEmpty()) {
                setMessage("")
                hide()
            } else {
                setMessage(error, Color.RED)
            }
        }

        findViewById<View>(R.id.signUp).setOnClickListener {
            mainActivity.signUp.show()
        }

        messageView = findViewById(R.id.message)
        setMessage(message, messageColor)

        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun hide() {
        setMessage("")
        super.hide()
    }

    fun setMessage(text: String, color: Int = Color.WHITE) {
        message = text
        messageColor = color
        messageView?.text = message
        messageView?.setTextColor(color)
    }
}