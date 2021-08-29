package com.kotme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
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

    fun currentProgressExercise(
        progressLiveData: LiveData<Int?> = userProgress().asLiveData()
    ): LiveData<Exercise?> = Transformations.switchMap(progressLiveData) {
        if (it != null) exerciseDao.get(it + 1).asLiveData() else null
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

    fun currentExerciseLiveData(
        currentExerciseLiveData: LiveData<Int?> = userDao.currentExerciseFlow().asLiveData()
    ): LiveData<Exercise?> = Transformations.switchMap(currentExerciseLiveData) {
        if (it != null) exerciseDao.get(it).asLiveData() else null
    }

    suspend fun saveCode(id: Int, code: String) {
        exerciseDao.setCode(id, code)
    }

    suspend fun checkCode(exercise: Exercise): CodeCheckResult =
        kotmeApi.checkCodeAnonym(exercise.id, exercise.userCode).also {
            exerciseDao.setResult(exercise.id, it.status, it.errors, it.consoleLog)
        }
}