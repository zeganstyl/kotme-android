package com.kotme.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class Exercise(
    @PrimaryKey val id: Int,
    val number: Int,
    val name: String,
    val lessonText: String,
    val storyText: String,
    val exerciseText: String,
    val initialCode: String,
    val characterMessage: String,
    @ColumnInfo(defaultValue = "")
    val userCode: String,
    @ColumnInfo(defaultValue = "0")
    val completeTime: Long,
    @ColumnInfo(defaultValue = "NoStatus")
    val resultStatus: CodeCheckResultStatus,
    @ColumnInfo(defaultValue = "")
    val resultMessage: String,
    @ColumnInfo(defaultValue = "")
    val resultConsole: String
) {
    constructor(
        id: Int,
        number: Int,
        name: String,
        lessonText: String,
        storyText: String,
        exerciseText: String,
        initialCode: String,
        characterMessage: String,
    ): this(
        id,
        number,
        name,
        lessonText,
        storyText,
        exerciseText,
        initialCode,
        characterMessage,
        "",
        0,
        CodeCheckResultStatus.NoStatus,
        "",
        ""
    )
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise WHERE id = :id")
    fun getFlow(id: Int): Flow<Exercise?>

    @Query("SELECT * FROM exercise WHERE id = :id")
    fun get(id: Int): Flow<Exercise?>

    @Query("SELECT * FROM exercise")
    fun all(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercise WHERE number = :number LIMIT 1")
    fun getByNumber(number: Int): Flow<Exercise>

    @Query("UPDATE exercise SET userCode = :code WHERE id = :id")
    suspend fun setCode(id: Int, code: String)

    @Query("UPDATE exercise SET resultStatus = :status, resultMessage = :message, resultConsole = :consoleLog WHERE id = :id")
    fun setResult(id: Int, status: CodeCheckResultStatus, message: String, consoleLog: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Exercise)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<Exercise>)
}