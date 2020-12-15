package com.thelemistix.kotme

import android.os.Bundle
import android.view.View

class MainMenuFragment(): FragmentBase(R.layout.main_menu, true) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.play).setOnClickListener {
            mainActivity.showCommon(mainActivity.lesson)
        }

        view.findViewById<View>(R.id.achievements).setOnClickListener {
            mainActivity.achievements.show()
        }

        view.findViewById<View>(R.id.legend).setOnClickListener {
            mainActivity.legend.show()
        }

        view.findViewById<View>(R.id.seashell).setOnClickListener {
            mainActivity.showFull(mainActivity.settings)
        }
    }
}
