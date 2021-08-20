package com.kotme

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView

class SystemMessageDialog(val mainActivity: MainActivity): Dialog(mainActivity) {
    var message: String = ""
        set(value) {
            field = value
            findViewById<TextView>(R.id.message)?.text = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.system_message)

        findViewById<TextView>(R.id.message)?.text = message

        findViewById<View>(R.id.next).setOnClickListener {
            hide()
        }

        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
