package com.kotme.markdown

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import com.kotme.markdown.FunctionDeclarationRule
import de.markusressel.kodehighlighter.core.StyleFactory
import de.markusressel.kodehighlighter.core.colorscheme.ColorScheme
import de.markusressel.kodehighlighter.core.rule.LanguageRule
import de.markusressel.kodehighlighter.language.kotlin.rule.*

class DarkBackgroundColorScheme : ColorScheme {
    override fun getStyles(type: LanguageRule): Set<StyleFactory> {
        return when (type) {
            is ImportKeywordRule,
            is PackageKeywordRule,
            is ClassKeywordRule,
            is OpenKeywordRule,
            is ReturnKeywordRule,
            is VisibilityKeywordRule,
            is FunctionKeywordRule,
            is VarKeywordRule -> setOf { ForegroundColorSpan(Color.parseColor("#CC7832")) }
            is FunctionDeclarationRule -> setOf { ForegroundColorSpan(Color.parseColor("#FFC66D")) }
            is AnnotationRule -> setOf { ForegroundColorSpan(Color.parseColor("#FF6D00")) }
            is CommentRule -> setOf { ForegroundColorSpan(Color.parseColor("#6A8759")) }
            is NumberRule -> setOf { ForegroundColorSpan(Color.parseColor("#01579B")) }
            else -> emptySet()
        }
    }
}
