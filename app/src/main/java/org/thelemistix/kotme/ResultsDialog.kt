package org.thelemistix.kotme

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView

class ResultsDialog(context: Context) : Dialog(context) {
    var messageView: TextView? = null
        set(value) {
            field = value
            value?.text = message
        }

    var consoleView: TextView? = null
        set(value) {
            field = value
            value?.text = console
        }

    var message: String = ""
        set(value) {
            field = value
            messageView?.text = value
        }

    var console: String = ""
        set(value) {
            field = value
            consoleView?.text = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.results)
        findViewById<View>(R.id.next).setOnClickListener {
            hide()
        }

        messageView = findViewById(R.id.message)
        consoleView = findViewById(R.id.console)

        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}