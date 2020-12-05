package org.thelemistix.kotme

import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println("=====")
        println(cacheDir.resolve("cache").readText())

        findViewById<View>(R.id.play).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
            startActivity(Intent(this, ExerciseActivity::class.java))
        }

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

        findViewById<View>(R.id.achievements).setOnClickListener {
            startActivity(Intent(this, AchievementsActivity::class.java))
        }

        findViewById<View>(R.id.legend).setOnClickListener {
            startActivity(Intent(this, LegendActivity::class.java))
        }

        findViewById<View>(R.id.seashell).setOnClickListener {
            startActivity(Intent(this, HiddenSettings::class.java))
        }
    }
}
