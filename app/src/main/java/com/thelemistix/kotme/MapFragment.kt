package com.thelemistix.kotme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class MapFragment() : FragmentBase(R.layout.map) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val res = resources

        for (i in 1 until 7) {
            val id = res.getIdentifier("exercise$i", "id", view.context.packageName)
            view.findViewById<View>(id).setOnClickListener {
                mainActivity.lesson.lesson = i
                mainActivity.lesson.show()
            }
        }
    }
}
