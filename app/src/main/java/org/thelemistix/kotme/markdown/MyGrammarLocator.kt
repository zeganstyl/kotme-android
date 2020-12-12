package org.thelemistix.kotme.markdown

import io.noties.prism4j.GrammarLocator
import io.noties.prism4j.Prism4j

class MyGrammarLocator : GrammarLocator {
    override fun grammar(prism4j: Prism4j, language: String): Prism4j.Grammar? {
        return when (language) {
            "clike" -> Prism_clike.create(prism4j)
            "kotlin" -> Prism_kotlin.create(prism4j)
            else -> null
        }
    }

    val languages = HashSet<String>().apply {
        add("kotlin")
        add("clike")
    }

    override fun languages(): MutableSet<String> = languages
}