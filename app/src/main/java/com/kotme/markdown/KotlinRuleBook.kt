package com.kotme.markdown

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