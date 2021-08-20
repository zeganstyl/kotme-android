package com.kotme.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class Achievement(
    @PrimaryKey val id: Int,
    val name: String,
    val conditionText: String,
    val received: Boolean = false
)

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievement")
    fun all(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: List<Achievement>)
}