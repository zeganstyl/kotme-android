package com.kotme

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

fun Fragment.kotmeViewModel() = viewModels<MapViewModel>()

fun DialogFragment.show() {
    dialog?.show()
}

fun DialogFragment.hide() {
    dialog?.hide()
}