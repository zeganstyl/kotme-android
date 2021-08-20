package com.kotme.markdown

import android.text.SpannableString
import android.widget.TextView
import de.markusressel.kodehighlighter.core.util.SpannableHighlighter
import de.markusressel.kodehighlighter.language.kotlin.colorscheme.DarkBackgroundColorScheme
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Highlighter @Inject constructor():
    SpannableHighlighter(KotlinRuleBook(), DarkBackgroundColorScheme()) {
    suspend fun highlight(view: TextView, code: String) {
        val spannable = SpannableString.valueOf(code)
        highlight(spannable)
        view.text = spannable
    }
}