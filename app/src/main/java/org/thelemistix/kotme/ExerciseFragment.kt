package org.thelemistix.kotme

import android.os.Bundle
import android.text.SpannableString
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.markusressel.kodehighlighter.core.util.SpannableHighlighter
import kotlinx.coroutines.*
import org.thelemistix.kotme.markdown.KotlinRuleBook

class ExerciseFragment(val mainActivity: MainActivity) : Fragment(R.layout.exercise) {
    var exercise: Int = 1

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
        val ruleBook = KotlinRuleBook()

        val highlighter = SpannableHighlighter(ruleBook, DarkBackgroundColorScheme())

        val code = view.findViewById<TextView>(R.id.code)
        code.text = "fun main() {\nprintln(\"Привет Котлин!\")\n}"

        val spannable = SpannableString.valueOf(code.text.toString())

        runBlocking {
            highlighter.highlight(spannable)
            code.text = spannable
        }

        view.findViewById<View>(R.id.description).setOnClickListener {
            mainActivity.exerciseDescription.show()
        }

        view.findViewById<View>(R.id.check).setOnClickListener {
            mainActivity.client.checkCode(code.text.toString(), exercise.toString())
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
