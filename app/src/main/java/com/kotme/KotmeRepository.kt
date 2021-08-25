package com.kotme

import androidx.lifecycle.MutableLiveData
import com.kotme.common.CodeCheckResult
import com.kotme.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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
                job?.cancel()
                job = scope.launch {
                    exerciseDao.get((progress ?: -2) + 1).collect {
                        liveData.value = it
                    }
                }
            }
        }
        return liveData
    }

    suspend fun getUpdates(from: Long) {
        try {
            kotmeApi.getUpdates(from).apply {
                achievementDao.insert(achievements.map { Achievement(it) })
                exerciseDao.insert(exercises.map { Exercise(it) })

                userDao.insert(User(user.name, user.progress, lastUpdateTime))
            }
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
        kotmeApi.checkCodeAnonym(exercise.id, exercise.userCode).also {
            println(it)
            exerciseDao.setResult(exercise.id, it.status, it.errors, it.consoleLog)
        }
}