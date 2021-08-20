package com.kotme

import androidx.lifecycle.MutableLiveData
import com.kotme.api.CodeCheckResult
import com.kotme.api.KotmeApi
import com.kotme.data.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class KotmeRepository @Inject constructor(
    val exerciseDao: ExerciseDao,
    val achievementDao: AchievementDao,
    val userDao: UserDao,
    private val kotmeApi: KotmeApi
) {
    fun userProgress() = userDao.getProgressFlow()

    fun currentProgressExerciseLiveData(scope: CoroutineScope): MutableLiveData<Exercise?> {
        val liveData = MutableLiveData<Exercise?>()
        var job: Job? = null
        scope.launch {
            userDao.getProgressFlow().collect { progress ->
                println("progress: $progress")
                job?.cancel()
                job = scope.launch {
                    exerciseDao.get((progress ?: -2) + 1).collect {
                        println("progress exe: $it")
                        liveData.value = it
                    }
                }
            }
        }
        return liveData
    }

    suspend fun getUpdates(from: Long) {
        try {
            val updates = kotmeApi.getUpdates(from)

            achievementDao.insert(updates.achievements.map { it.entity() })
            exerciseDao.insert(updates.exercises.map { it.entity() })

            userDao.insert(User(updates.user.name, updates.user.progress, 1, updates.lastUpdateTime))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setCurrentExercise(number: Int) {
    }

    fun currentExerciseLiveData(scope: CoroutineScope): MutableLiveData<Exercise?> {
        val liveData = MutableLiveData<Exercise?>()
        var job: Job? = null
        scope.launch {
            userDao.currentExerciseFlow().collect {
                job?.cancel()
                job = scope.launch {
                    exerciseDao.get(it ?: -1).collect {
                        liveData.value = it
                    }
                }
            }
        }
        return liveData
    }

    suspend fun saveCode(id: Int, code: String) {
        exerciseDao.setCode(id, code)
    }

    suspend fun checkCode(exercise: Exercise): CodeCheckResult =
        kotmeApi.checkCode(exercise.id, exercise.userCode).also {
            exerciseDao.setResult(exercise.id, it.status, it.message, it.consoleLog)
        }
}