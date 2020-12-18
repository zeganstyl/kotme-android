package com.thelemistix.kotme

import android.content.res.AssetManager
import android.database.Cursor
import android.database.DatabaseUtils
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import kotlin.collections.ArrayList

class DB(val mainActivity: MainActivity) {
    val db = mainActivity.openOrCreateDatabase(KotmeClient.Origin, AppCompatActivity.MODE_PRIVATE, null)

    var progress: Int = 0
        private set

    val achievements: List<Int>
        get() = achievementsInternal

    private val achievementsInternal = ArrayList<Int>()

    init {
        recreateTable(ExerciseTable, "text TEXT, task TEXT, initial_code TEXT")
        execSqlFile("db/kotme.sql")

        recreateTable(AchievementTable, "name TEXT, prim TEXT")
        execSqlFile("db/achivment.sql")

        recreateTable(ReplicTable, "text TEXT")
        execSqlFile("db/character_replics.sql")

        createTable(ExerciseCacheTable, "code TEXT, checked BIT, date INTEGER")

        if (mainActivity.prefs.contains("progress")) {
            setProgress(mainActivity.prefs.getString("progress", "{}")!!)
        }
    }

    fun getReplic(step: Int): String {
        val cursor = query(ReplicTable, "id = $step")
        val text = unescape(cursor.getString(1))
        cursor.close()
        return text
    }

    fun iterateAchievementDescriptions(block: (id: Int, name: String, description: String) -> Unit) {
        val cursor = query(AchievementTable)

        for (i in 0 until cursor.count) {
            val exercise = cursor.getInt(0)
            val name = cursor.getString(1)
            val desc = cursor.getString(2)

            block(exercise, name, desc)

            cursor.moveToNext()
        }

        cursor.close()
    }

    fun getAchievement(id: Int, block: (name: String, description: String) -> Unit) {
        val cursor = query(AchievementTable, "id = $id")
        val name = cursor.getString(1)
        val desc = cursor.getString(2)
        block(name, desc)
        cursor.close()
    }

    private fun execSqlFile(name: String) {
        val am: AssetManager = mainActivity.assets
        val stream = am.open(name)
        db.execSQL(stream.readBytes().toString(Charset.defaultCharset()))
    }

    fun checkAllUncheckedCode() {
        val checked = ArrayList<Int>()

        val cursor = query(ExerciseCacheTable, "checked = 0")
        for (i in 0 until cursor.count) {
            val exercise = cursor.getString(0)
            val code = cursor.getString(1)

            checked.add(exercise.toInt())
            mainActivity.client.checkCode(code, exercise, true)

            cursor.moveToNext()
        }

        checked.forEach { markCodeChecked(it, true) }

        cursor.close()
    }

    fun setProgress(json: String, showNewAchievements: Boolean = false) {
        try {
            val progressJson = JSONObject(json)
            if (progressJson.has("progress")) progress = progressJson.getInt("progress")

            val oldAchivs = ArrayList<Int>()
            oldAchivs.addAll(achievementsInternal)

            achievementsInternal.clear()
            if (progressJson.has("achievements")) {
                val achievementsJson = progressJson.getJSONArray("achievements")
                for (i in 0 until achievementsJson.length()) {
                    achievementsInternal.add(achievementsJson.getInt(i))
                }
            }

            mainActivity.db.cacheProgress()

            // new achievements
            if (showNewAchievements) {
                achievementsInternal.subtract(oldAchivs).forEach {
                    getAchievement(it) { name, _ ->
                        mainActivity.client.toast("Получено достижение:\n$name")
                    }
                }
            }

            mainActivity.toolbar.progress = progress

            mainActivity.achievements.setAchievements()
        } catch (ex: Exception) {}
    }

    fun cacheProgress() {
        val progressJson = JSONObject()
        progressJson.put("progress", progress)

        val achievementsJson = JSONArray()
        achievements.forEach { achievementsJson.put(it) }
        progressJson.put("progress", progress)
        progressJson.put("achievements", achievementsJson)

        mainActivity.prefs.edit().putString("progress", progressJson.toString(4)).apply()
    }

    private fun dropTable(name: String) {
        db.execSQL("DROP TABLE IF EXISTS $name")
    }

    /** @param columns first column must not be id, because it is already included */
    private fun createTable(name: String, columns: String) {
        db.execSQL("""
CREATE TABLE IF NOT EXISTS $name (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  $columns
);
""")
    }

    private fun query(table: String, selection: String? = null): Cursor {
        val cursor = db.query(table, null, selection, null, null, null, null, null)
        cursor.moveToNext()
        return cursor
    }

    fun recreateTable(name: String, columns: String) {
        dropTable(name)
        createTable(name, columns)
    }

    /** If there is cached code, it will be returned, otherwise will be returned default initial code */
    fun getExercise(exercise: Int, cursorBlock: (storyText: String, description: String, initialCode: String) -> Unit) {
        mainActivity.client.syncExercise(exercise)

        val cursor = query(ExerciseTable, "id = $exercise")

        val storyText = unescape(cursor.getString(1))
        val description = unescape(cursor.getString(2))
        val initialCode = unescape(cursor.getString(3))

        cursor.close()

        cursorBlock(storyText, description, initialCode)
    }

    fun getExerciseCache(exercise: Int, cursorBlock: (code: String) -> Unit) {
        val cursor = query(ExerciseCacheTable, "id = $exercise")
        if (cursor.count > 0) cursorBlock(unescape(cursor.getString(1)))
        cursor.close()
    }

    private fun unescape(text: String) = text
        .replace("\\r\\n", "\n")
        .replace("\\n", "\n")
        .replace("\\\"", "\"")
        .replace("\\\'", "\'")

    fun currentTime() = (System.currentTimeMillis() / 1000).toString()

    fun setExerciseCache(exercise: Int, code: String, checked: Boolean, date: Long? = null) {
        val cursor = query(ExerciseCacheTable, "id = $exercise")

        val code2 = DatabaseUtils.sqlEscapeString(code)
        val checked2 = if(checked) 1 else 0
        val date2 = date ?: currentTime()

        if (cursor.count == 0) {
            db.execSQL("""
INSERT INTO `$ExerciseCacheTable` (`id`, `code`, `checked`, `date`) VALUES
($exercise, $code2, $checked2, $date2);
""")
        } else {
            db.execSQL("""
UPDATE `$ExerciseCacheTable`
SET code = $code2, checked = $checked2, date = $date2
WHERE id = $exercise;
""")
        }
    }

    fun markCodeChecked(exercise: Int, checked: Boolean, date: Long? = null) {
        val checked2 = if(checked) 1 else 0
        val date2 = date ?: currentTime()

        db.execSQL("""
UPDATE `$ExerciseCacheTable`
SET checked = $checked2, date = $date2
WHERE id = $exercise;
""")
    }

    companion object {
        const val ExerciseTable = "task"
        const val ExerciseCacheTable = "exercise_cache"
        const val AchievementTable = "achivment"
        const val ReplicTable = "character_replics"
    }
}
