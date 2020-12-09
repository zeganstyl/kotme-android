package org.thelemistix.kotme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class CommonActivity(val layoutId: Int): AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        supportActionBar?.hide()
    }
}
