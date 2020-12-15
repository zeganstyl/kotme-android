package com.thelemistix.kotme

import androidx.fragment.app.Fragment

open class FragmentBase(
    layout: Int,
    val full: Boolean = false,
    val stack: Boolean = true
): Fragment(layout) {
    val mainActivity: MainActivity
        get() = activity as MainActivity

    open fun show() {
        if (full) {
            mainActivity.showFull(this, stack)
        } else {
            mainActivity.showCommon(this, stack)
        }
    }
}
