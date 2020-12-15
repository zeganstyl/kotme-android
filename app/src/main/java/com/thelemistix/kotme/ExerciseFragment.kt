package com.thelemistix.kotme

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import kotlinx.coroutines.runBlocking

class ExerciseFragment() : FragmentBase(R.layout.exercise) {
    private var codeView: EditText? = null

    var exercise: Int = 1
        set(value) {
            field = value
            mainActivity.exerciseDescription.exercise = value

            val codeView = codeView
            if (codeView != null) {
                mainActivity.db.getExercise(value) { _, _, initialCode ->
                    mainActivity.setHighlightedCode(codeView, initialCode)
                }
            }
        }

    private var resultsButton: Button? = null
        set(value) {
            field = value
            value?.text = resultsButtonText
        }

    var resultsButtonText: String = ""
        set(value) {
            field = value
            resultsButton?.text = value
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        codeView = view.findViewById(R.id.code)
        val codeView = codeView!!
        codeView.addTextChangedListener {
            if (it != null) {
                runBlocking {
                    mainActivity.highlighter.highlight(it)
                }
            }
        }

        exercise = exercise

        view.findViewById<View>(R.id.description).setOnClickListener {
            mainActivity.exerciseDescription.show()
        }

        view.findViewById<View>(R.id.check).setOnClickListener {
            val code = codeView.text.toString()
            val checked = mainActivity.client.checkCode(code, exercise.toString())
            mainActivity.db.setExerciseCache(exercise, code, checked)
        }

        resultsButton = view.findViewById(R.id.results)
        resultsButton?.isActivated = resultsButtonText.isNotEmpty()
        resultsButton?.setOnClickListener {
            if (resultsButtonText.isNotEmpty()) {
                mainActivity.results.show()
            }
        }
    }
}
