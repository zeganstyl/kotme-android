package com.kotme.common

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

const val KotmeOrigin = "com.kotme"

fun DialogFragment.show() {
    dialog?.show()
}

fun DialogFragment.hide() {
    dialog?.hide()
}