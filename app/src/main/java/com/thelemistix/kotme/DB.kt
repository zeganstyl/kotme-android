package com.thelemistix.kotme

import android.content.res.AssetManager
import android.database.Cursor
import android.database.DatabaseUtils
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset

class DB(val mainActivity: MainActivity) {
    val db = mainActivity.openOrCreateDatabase(KotmeClient.Origin, AppCompatActivity.MODE_PRIVATE, null)

    var progress: Int = 0
        private set

    val achievements: List<Int>
        get() = achievementsInternal

    private val achievementsInternal = ArrayList<Int>()

    init {
        val am: AssetManager = mainActivity.assets
        val stream = am.open("db/kotme.sql")
        val s = stream.readBytes().toString(Charset.defaultCharset())

        recreateTable(ExerciseTable, "text TEXT, task TEXT, initial_code")
        createTable(ExerciseCacheTable, "code TEXT, checked BIT")
        db.execSQL(s)

        val progressFile = mainActivity.cacheDir.resolve("progress")
        if (progressFile.exists()) setProgress(progressFile.readText())
    }

    fun setProgress(json: String) {
        try {
            val progressJson = JSONObject(json)
            if (progressJson.has("progress")) progress = progressJson.getInt("progress")
            if (progressJson.has("achievements")) {
                val achievementsJson = progressJson.getJSONArray("achievements")
                for (i in 0 until achievementsJson.length()) {
                    achievementsInternal.add(achievementsJson.getInt(i))
                }
            }

            mainActivity.toolbar.progress = progress
        } catch (ex: Exception) {}
    }

    fun cacheProgress() {
        val progressJson = JSONObject()
        progressJson.put("progress", progress)

        val achievementsJson = JSONArray()
        achievements.forEach { achievementsJson.put(it) }
        progressJson.put("progress", progress)
        progressJson.put("achievements", achievementsJson)
        mainActivity.cacheDir.resolve("progress").writeText(progressJson.toString(4))
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
        val cursor = query(ExerciseTable, "id = $exercise")

        val storyText = unescape(cursor.getString(1))
        val description = unescape(cursor.getString(2))
        var initialCode = unescape(cursor.getString(3))

        cursor.close()

        getExerciseCache(exercise) {
            initialCode = it
        }

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

    fun iterateUncheckedExercises(cursorBlock: (exercise: Int, code: String) -> Unit) {
        val cursor = query(ExerciseCacheTable, "checked = 0")
        for (i in 0 until cursor.count) {
            cursorBlock(i + 1, cursor.getString(1))
            cursor.moveToNext()
        }
        cursor.close()
    }

    fun setExerciseCache(exercise: Int, code: String, checked: Boolean) {
        val cursor = query(ExerciseCacheTable, "id = $exercise")
        if (cursor.count == 0) {
            db.execSQL("""
INSERT INTO `$ExerciseCacheTable` (`id`, `code`, `checked`) VALUES
($exercise, ${DatabaseUtils.sqlEscapeString(code)}, ${if(checked) 1 else 0});
""")
        } else {
            db.execSQL("""
UPDATE `$ExerciseCacheTable`
SET code = ${DatabaseUtils.sqlEscapeString(code)}, checked = ${if(checked) 1 else 0}
WHERE id = $exercise;
""")
        }
    }

    fun resetUserTables() {
        recreateTable(ExerciseCacheTable, "code TEXT, checked BIT")
    }

    companion object {
        const val ExerciseTable = "task"
        const val ExerciseCacheTable = "exercise_cache"
    }
}
