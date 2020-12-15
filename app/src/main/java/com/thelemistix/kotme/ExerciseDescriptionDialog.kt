package com.thelemistix.kotme

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView

class ExerciseDescriptionDialog(val mainActivity: MainActivity) : Dialog(mainActivity) {
    var exercise: Int = 1
        set(value) {
            field = value
            findViewById<TextView>(R.id.title)?.text = "Задание $value"

            mainActivity.db.getExercise(value) { storyText, description, initialCode ->
                findViewById<TextView>(R.id.storyText)?.text = storyText
                findViewById<TextView>(R.id.description)?.text = description

                val codeView = findViewById<TextView>(R.id.initialCode)
                if (codeView != null) {
                    mainActivity.setHighlightedCode(codeView, initialCode)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.exercise_description)

        findViewById<View>(R.id.next).setOnClickListener {
            hide()
        }

        exercise = exercise

        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
