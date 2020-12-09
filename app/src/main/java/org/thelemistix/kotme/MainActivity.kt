package org.thelemistix.kotme

import android.content.res.AssetManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {
    val mainMenu = MainMenuFragment(this)
    val toolbar = ToolbarFragment(this)
    val achievements = AchievementsFragment(this)
    val legend = LegendFragment()
    val exercise = ExerciseFragment(this)
    val map = MapFragment()
    val hiddenSettings = HiddenSettingsFragment(this)

    lateinit var exerciseDescription: ExerciseDescriptionDialog
    lateinit var congratulations: CongratulationsDialog

    private fun fragmentTransaction(): FragmentTransaction =
        hideCommon(supportFragmentManager.beginTransaction())

    private fun hideCommon(transaction: FragmentTransaction) = transaction
        .hide(achievements)
        .hide(legend)
        .hide(exercise)
        .hide(map)

    fun showCommon(fragment: Fragment, stack: Boolean = true) {
        val t = fragmentTransaction()
        t.hide(mainMenu)
        if (toolbar.isHidden) t.show(toolbar)
        t.show(fragment)
        if (stack) t.addToBackStack(null)
        t.commit()
    }

    fun showFull(fragment: Fragment, stack: Boolean = true) {
        val t = fragmentTransaction()
        t.hide(toolbar)
        t.show(fragment)
        if (stack) t.addToBackStack(null)
        t.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        exerciseDescription = ExerciseDescriptionDialog(this)
        congratulations = CongratulationsDialog(this)

        println("=====")
        println(cacheDir.resolve("cache").readText())

        val db = openOrCreateDatabase("org.thelemistix.kotme", MODE_PRIVATE, null)

        val am: AssetManager = assets
        val stream = am.open("db/kotme.sql")
        val s = stream.readBytes().toString(Charset.defaultCharset())

        db.execSQL(DbHelper.DROP_TABLE)
        db.execSQL(DbHelper.CREATE_TABLE)
        db.execSQL(s)

        //db.query()

        val cursor = db.query("task", null, null, null, null, null, null, null)
        cursor.moveToNext()
        println(cursor.getString(1))

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .add(R.id.full, mainMenu)
            .add(R.id.full, toolbar)
            .add(R.id.common, achievements)
            .add(R.id.common, legend)
            .add(R.id.common, exercise)
            .add(R.id.common, map)
            .commit()

        showFull(mainMenu)
    }
}
