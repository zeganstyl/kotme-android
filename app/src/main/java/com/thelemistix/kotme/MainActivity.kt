package com.thelemistix.kotme

import android.os.Bundle
import android.text.SpannableString
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import de.markusressel.kodehighlighter.core.util.SpannableHighlighter
import kotlinx.coroutines.runBlocking
import com.thelemistix.kotme.markdown.KotlinRuleBook
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatDelegate

import android.content.SharedPreferences




class MainActivity : AppCompatActivity() {
    val mainMenu = MainMenuFragment()
    val toolbar = ToolbarFragment()
    val achievements = AchievementsFragment()
    val legend = LegendFragment()
    val exercise = ExerciseFragment()
    val lesson = LessonFragment()
    val map = MapFragment()
    val settings = HiddenSettingsFragment()

    lateinit var exerciseDescription: ExerciseDescriptionDialog
    lateinit var congratulations: CongratulationsDialog
    lateinit var results: ResultsDialog
    lateinit var login: LoginDialog
    lateinit var signUp: SignUpDialog
    lateinit var systemMessage: SystemMessageDialog

    val client = KotmeClient(this)

    val highlighter = SpannableHighlighter(KotlinRuleBook(), DarkBackgroundColorScheme())

    lateinit var db: DB

    val fragmentStack = ArrayList<FragmentBase>()
    private var stackEnabled = true

    lateinit var prefs: SharedPreferences

    private fun fragmentTransaction(): FragmentTransaction =
        hideCommon(supportFragmentManager.beginTransaction())

    private fun hideCommon(transaction: FragmentTransaction) = transaction
        .hide(achievements)
        .hide(legend)
        .hide(exercise)
        .hide(lesson)
        .hide(map)

    fun showCommon(fragment: FragmentBase, stack: Boolean = true) {
        val t = fragmentTransaction()
        t.hide(mainMenu)
        if (toolbar.isHidden) t.show(toolbar)
        t.show(fragment)
        if (stack && stackEnabled) fragmentStack.add(fragment)
        t.commit()
    }

    fun showFull(fragment: FragmentBase, stack: Boolean = true) {
        val t = fragmentTransaction()
        t.hide(toolbar)
        t.hide(settings)
        t.show(fragment)
        if (stack && stackEnabled) fragmentStack.add(fragment)
        t.commit()
    }

    fun setHighlightedCode(view: TextView, code: String) {
        runBlocking {
            val spannable = SpannableString.valueOf(code)
            highlighter.highlight(spannable)
            view.text = spannable
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        window.navigationBarColor = -0xFFFFFFF // black

        exerciseDescription = ExerciseDescriptionDialog(this)
        congratulations = CongratulationsDialog(this)
        results = ResultsDialog(this)
        login = LoginDialog(this)
        signUp = SignUpDialog(this)
        systemMessage = SystemMessageDialog(this)

        prefs = getSharedPreferences(KotmeClient.Origin, MODE_PRIVATE)

        db = DB(this)

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .add(R.id.full, mainMenu)
            .add(R.id.full, toolbar)
            .add(R.id.full, settings)
            .add(R.id.common, achievements)
            .add(R.id.common, legend)
            .add(R.id.common, exercise)
            .add(R.id.common, lesson)
            .add(R.id.common, map)
            .commit()

        showFull(mainMenu)

        client.loadServerConfig()

        when (client.signIn()) {
            SignInStatus.Fail -> login.show()
            SignInStatus.OK -> {
                client.syncProgress()
            }
        }
    }

    fun back() {
        if (fragmentStack.size > 1) {
            stackEnabled = false
            fragmentStack.removeLast()
            fragmentStack.last().show()
            stackEnabled = true
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            back()
            true
        } else super.onKeyDown(keyCode, event)
    }
}
