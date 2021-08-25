package com.kotme.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

const val KotmeOrigin = "com.kotme"

fun DialogFragment.prepare() {
    dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
}