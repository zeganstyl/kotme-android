package com.thelemistix.kotme

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class ToolbarFragment(): FragmentBase(R.layout.toolbar, full = true, stack = false) {
    private var progressBar: ProgressBar? = null
    private var progressText: TextView? = null

    var progress: Int = 0
        set(value) {
            field = value
            progressBar?.progress = progress
            progressText?.text = "$progress/10"
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.back).setOnClickListener {
            mainActivity.back()
        }

        view.findViewById<View>(R.id.menu).setOnClickListener {
            mainActivity.mainMenu.show()
        }

        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText)

        progress = progress
    }
}
