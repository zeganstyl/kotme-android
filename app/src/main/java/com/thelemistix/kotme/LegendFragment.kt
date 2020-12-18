package com.thelemistix.kotme

import android.os.Bundle
import android.view.View

class LegendFragment(): FragmentBase(R.layout.legend) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.next).setOnClickListener {
            mainActivity.map.show()
        }
    }
}
