package org.thelemistix.kotme

import android.os.Bundle
import android.text.SpannableString
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.markusressel.kodehighlighter.core.util.SpannableHighlighter
import kotlinx.coroutines.*

class ExerciseFragment(val mainActivity: MainActivity) : Fragment(R.layout.exercise) {
    var exercise: Int = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val am = AccountManager.get(this) // "this" references the current Context
//        val accounts = am.getAccountsByType("org.thelemistix.kotme")
//        am.getPassword(accounts[0])
//
//
//        val account = Account("user", "org.thelemistix.kotme")
//        am.addAccountExplicitly(account, "password", null)


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

        view.findViewById<View>(R.id.results).setOnClickListener {
            mainActivity.results.show()
        }
    }
}
