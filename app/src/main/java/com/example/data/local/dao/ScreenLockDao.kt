package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.ScreenLockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenLockDao {
    @Query("SELECT * FROM screen_locks ORDER BY triggerHour ASC, triggerMinute ASC")
    fun getAllLocksFlow(): Flow<List<ScreenLockEntity>>

    @Query("SELECT * FROM screen_locks WHERE id = :id LIMIT 1")
    suspend fun getLockById(id: String): ScreenLockEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLock(lock: ScreenLockEntity)

    @Update
    suspend fun updateLock(lock: ScreenLockEntity)

    @Delete
    suspend fun deleteLock(lock: ScreenLockEntity)

    @Query("DELETE FROM screen_locks WHERE id = :id")
    suspend fun deleteLockById(id: String)
}
