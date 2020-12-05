package org.thelemistix.kotme

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class LegendActivity : CommonActivity(R.layout.activity_legend) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<Button>(R.id.next).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
    }
}
