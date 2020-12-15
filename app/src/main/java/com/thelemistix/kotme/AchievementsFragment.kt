package com.thelemistix.kotme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class AchievementsFragment(): FragmentBase(R.layout.achievements) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.next).setOnClickListener {
            mainActivity.showFull(mainActivity.mainMenu)
        }
    }

    fun syncProgress() {
        // TODO
    }
}
