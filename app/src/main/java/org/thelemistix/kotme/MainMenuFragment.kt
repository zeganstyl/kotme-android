package org.thelemistix.kotme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class MainMenuFragment(val mainActivity: MainActivity): Fragment(R.layout.main_menu) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.play).setOnClickListener {
            mainActivity.showCommon(mainActivity.lesson)
        }

        view.findViewById<View>(R.id.achievements).setOnClickListener {
            mainActivity.showCommon(mainActivity.achievements)
        }

        view.findViewById<View>(R.id.legend).setOnClickListener {
            mainActivity.showCommon(mainActivity.legend)
        }

        view.findViewById<View>(R.id.seashell).setOnClickListener {
            mainActivity.showFull(mainActivity.hiddenSettings)
        }
    }
}
