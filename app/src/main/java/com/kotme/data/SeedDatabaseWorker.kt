package com.kotme.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SeedDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    fun readFile(file: String): String =
        String(applicationContext.assets.open(file).readBytes())

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val database = KotmeDatabase.getInstance(applicationContext)
        database.userDao().insert(User("", 0, id = User.ID))
        database.exerciseDao().insert(
            Exercise(
                1,
                1,
                "",
                readFile("lessons/1lesson.md"),
                readFile("lessons/1story.txt"),
                readFile("lessons/1exercise.txt"),
                readFile("lessons/1code.kt"),
                readFile("lessons/1talk.txt")
            )
        )

        Result.success()
    }
}