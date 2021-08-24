package com.kotme.data

import androidx.room.*
import com.kotme.common.AchievementDTO
import kotlinx.coroutines.flow.Flow

@Entity
data class Achievement(
    @PrimaryKey val id: Int,
    val name: String,
    val conditionText: String,
    val received: Boolean = false
) {
    constructor(dto: AchievementDTO): this(
        dto.id,
        dto.name,
        dto.conditionText
    )
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievement")
    fun all(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: List<Achievement>)
}