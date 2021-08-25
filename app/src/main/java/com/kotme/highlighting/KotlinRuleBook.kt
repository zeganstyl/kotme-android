package com.kotme.highlighting

import com.kotme.highlighting.DarkBackgroundColorScheme
import com.kotme.highlighting.FunctionDeclarationRule
import de.markusressel.kodehighlighter.core.LanguageRuleBook
import de.markusressel.kodehighlighter.core.colorscheme.ColorScheme
import de.markusressel.kodehighlighter.core.rule.LanguageRule
import de.markusressel.kodehighlighter.language.kotlin.rule.*

class KotlinRuleBook : LanguageRuleBook {
    override val defaultColorScheme: ColorScheme = DarkBackgroundColorScheme()

    override fun getRules(): Set<LanguageRule> {
        return setOf(
            FunctionKeywordRule(),
            FunctionDeclarationRule(),
            AnnotationRule(),
                ClassKeywordRule(),
                CommentRule(),
                ImportKeywordRule(),
                PackageKeywordRule(),
                ReturnKeywordRule(),
                VarKeywordRule(),
                NumberRule())
    }
}