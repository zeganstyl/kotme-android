package org.thelemistix.kotme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class ToolbarFragment(val mainActivity: MainActivity): Fragment(R.layout.toolbar) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.back).setOnClickListener {
            fragmentManager?.popBackStack()
        }

        view.findViewById<View>(R.id.menu).setOnClickListener {
            mainActivity.showFull(mainActivity.mainMenu)
        }
    }
}
