package org.thelemistix.kotme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import io.noties.markwon.Markwon
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j

class MainMenuFragment(val mainActivity: MainActivity): Fragment(R.layout.main_menu) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.play).setOnClickListener {
            mainActivity.showCommon(mainActivity.exercise)
        }

        view.findViewById<View>(R.id.achievements).setOnClickListener {
            mainActivity.showCommon(mainActivity.achievements)
        }

        view.findViewById<View>(R.id.legend).setOnClickListener {
            mainActivity.showCommon(mainActivity.legend)
        }

        view.findViewById<View>(R.id.seashell).setOnClickListener {
            mainActivity.showFull(mainActivity.hiddenSettings)
        }

        val prism4j = Prism4j(MyGrammarLocator())
        // obtain an instance of Markwon
        val markwon = Markwon.builder(view.context)
            .usePlugin(SyntaxHighlightPlugin.create(prism4j, Prism4jThemeDarkula.create()))
            .build()
        markwon.setMarkdown(view.findViewById(R.id.markdown), """
#### Основной синтаксис

На Котлине исполнение любой программы, как и во многих других языках, начинается с входной точки - функции main(). Любая функция в Котлине обявляется с помощью ключевого слова fun. И далее, в фигурных скобках { }, мы пишем какой-нибудь код.

```kotlin
fun main() {
    // код
}
```
""".trimIndent())
    }
}
