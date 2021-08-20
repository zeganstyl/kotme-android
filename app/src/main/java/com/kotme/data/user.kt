package com.kotme.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
class User(
    val name: String,
    val progress: Int,
    @PrimaryKey
    val id: Int = ID,
    val dataUpdateTime: Long = 0,
    val currentExercise: Int = 1
) {
    companion object {
        const val ID = 1
    }
}

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("DELETE FROM user")
    suspend fun delete()

    @Query("UPDATE user SET name = :name, progress = :progress WHERE id = ${User.ID}")
    suspend fun update(name: String, progress: Int)

    @Query("SELECT * FROM user WHERE id = ${User.ID} LIMIT 1")
    fun getFlow(): Flow<User?>

    @Query("SELECT * FROM user WHERE id = ${User.ID} LIMIT 1")
    suspend fun get(): User?

    @Query("SELECT name FROM user WHERE id = ${User.ID} LIMIT 1")
    fun getName(): Flow<String?>

    @Query("UPDATE user SET dataUpdateTime = :time")
    suspend fun setDataUpdateTime(time: Long)

    @Query("SELECT progress FROM user WHERE id = ${User.ID}")
    fun getProgressFlow(): Flow<Int?>

    @Query("SELECT currentExercise FROM user WHERE id = ${User.ID}")
    fun currentExerciseFlow(): Flow<Int?>

    @Query("UPDATE user SET currentExercise = :number WHERE id = ${User.ID}")
    suspend fun setCurrentExercise(number: Int)
}