package org.thelemistix.kotme

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

abstract class CommonActivity(val layoutId: Int): AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        supportActionBar?.hide()

        val toolbar = findViewById<View>(R.id.toolbar)

        toolbar.findViewById<View>(R.id.back).setOnClickListener {
            finish()
        }

        toolbar.findViewById<View>(R.id.menu).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}