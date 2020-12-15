package com.thelemistix.kotme

import android.content.res.AssetManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.noties.markwon.Markwon
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import com.thelemistix.kotme.markdown.MyGrammarLocator
import java.nio.charset.Charset

class LessonFragment(): FragmentBase(R.layout.lesson) {
    private var lessonView: TextView? = null

    var lesson: Int = 1
        set(value) {
            field = value

            val lessonView = lessonView
            if (lessonView != null) {
                val am: AssetManager = mainActivity.assets
                val stream = am.open("lessons/lesson$value.md")
                markdown.setMarkdown(lessonView, stream.readBytes().toString(Charset.defaultCharset()))
            }
        }

    lateinit var markdown: Markwon

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.next).setOnClickListener {
            mainActivity.exercise.exercise = lesson
            mainActivity.exercise.show()
        }

        val prism4j = Prism4j(MyGrammarLocator())
        markdown = Markwon.builder(view.context)
            .usePlugin(SyntaxHighlightPlugin.create(prism4j, Prism4jThemeDarkula.create()))
            .build()

        lessonView = view.findViewById(R.id.lessonText)
        lesson = lesson
    }
}
