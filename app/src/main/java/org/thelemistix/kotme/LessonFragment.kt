package org.thelemistix.kotme

import android.content.res.AssetManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import io.noties.markwon.Markwon
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import org.thelemistix.kotme.markdown.MyGrammarLocator
import java.nio.charset.Charset

class LessonFragment(val mainActivity: MainActivity): Fragment(R.layout.lesson) {
    var lessonText: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.next).setOnClickListener {
            mainActivity.showCommon(mainActivity.exercise)
        }

        val prism4j = Prism4j(MyGrammarLocator())
        val markdown = Markwon.builder(view.context)
            .usePlugin(SyntaxHighlightPlugin.create(prism4j, Prism4jThemeDarkula.create()))
            .build()

        val am: AssetManager = mainActivity.assets
        val stream = am.open("lessons/lesson1.md")
        lessonText = stream.readBytes().toString(Charset.defaultCharset())

        markdown.setMarkdown(view.findViewById(R.id.lessonText), lessonText)
    }
}
