package com.kotme.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kotme.common.CodeCheckResultStatus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(entities = [Exercise::class, Achievement::class, User::class], version = 1)
@TypeConverters(Converters::class)
abstract class KotmeDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun achievementDao(): AchievementDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var instance: KotmeDatabase? = null

        fun getInstance(context: Context): KotmeDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): KotmeDatabase {
            //context.deleteDatabase(DATABASE_NAME)
            return Room.databaseBuilder(context, KotmeDatabase::class.java, DATABASE_NAME)
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                            WorkManager.getInstance(context).enqueue(request)
                        }
                    }
                )
                .build()
        }

        const val DATABASE_NAME = "kotme-db"
    }
}

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun appDatabase(@ApplicationContext context: Context): KotmeDatabase =
        KotmeDatabase.getInstance(context)

    @Provides
    fun exerciseDao(db: KotmeDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    fun achievementDao(db: KotmeDatabase): AchievementDao = db.achievementDao()

    @Singleton
    @Provides
    fun userDao(db: KotmeDatabase): UserDao = db.userDao()
}

class Converters {
    @TypeConverter
    fun toCodeCheckResultStatus(value: String) = CodeCheckResultStatus.valueOf(value)

    @TypeConverter
    fun fromCodeCheckResultStatus(value: CodeCheckResultStatus) = value.name
}