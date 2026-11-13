package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.RoutineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines ORDER BY triggerHour ASC, triggerMinute ASC")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE isEnabled = 1")
    fun getEnabledRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun getRoutineById(id: String): RoutineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(routines: List<RoutineEntity>)

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Query("DELETE FROM routines WHERE id = :id")
    suspend fun deleteRoutineById(id: String)
}
