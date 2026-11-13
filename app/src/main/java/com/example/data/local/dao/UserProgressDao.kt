package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE dateKey = :dateKey LIMIT 1")
    fun getProgressByDate(dateKey: String): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress ORDER BY dateKey DESC LIMIT 7")
    fun getRecentProgress(): Flow<List<UserProgressEntity>>

    @Query("SELECT * FROM user_progress ORDER BY id DESC LIMIT 1")
    suspend fun getLatestProgress(): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET ayahsReadToday = ayahsReadToday + :count, totalLifetimeAyahs = totalLifetimeAyahs + :count WHERE dateKey = :dateKey")
    suspend fun recordAyahs(dateKey: String, count: Int)

    @Query("UPDATE user_progress SET routinesCompletedToday = routinesCompletedToday + 1, streakDays = streakDays + 1 WHERE dateKey = :dateKey")
    suspend fun recordRoutineCompleted(dateKey: String)
}
