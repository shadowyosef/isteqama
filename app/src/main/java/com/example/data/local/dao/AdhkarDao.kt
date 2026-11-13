package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.AdhkarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdhkarDao {
    @Query("SELECT * FROM adhkar ORDER BY orderIndex ASC")
    fun getAllAdhkar(): Flow<List<AdhkarEntity>>

    @Query("SELECT * FROM adhkar WHERE category = :category ORDER BY orderIndex ASC")
    fun getAdhkarByCategory(category: String): Flow<List<AdhkarEntity>>

    @Query("SELECT * FROM adhkar WHERE isCustom = 1")
    fun getCustomAdhkar(): Flow<List<AdhkarEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdhkar(item: AdhkarEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AdhkarEntity>)

    @Query("UPDATE adhkar SET currentCount = :count, isCompleted = :completed WHERE id = :id")
    suspend fun updateCount(id: String, count: Int, completed: Boolean)

    @Query("UPDATE adhkar SET currentCount = 0, isCompleted = 0 WHERE category = :category")
    suspend fun resetCategory(category: String)

    @Delete
    suspend fun delete(item: AdhkarEntity)
}
