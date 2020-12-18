package com.thelemistix.kotme

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.os.Bundle
import android.view.Window
import android.view.WindowManager

class CongratulationsDialog(val mainActivity: MainActivity) : Dialog(mainActivity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.congratulations)
        findViewById<View>(R.id.next).setOnClickListener {
            hide()
            mainActivity.map.show()
        }

        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
