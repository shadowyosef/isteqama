package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AppPreferencesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppPreferencesDao {
    @Query("SELECT * FROM app_preferences WHERE id = 1 LIMIT 1")
    fun getPreferences(): Flow<AppPreferencesEntity?>

    @Query("SELECT * FROM app_preferences WHERE id = 1 LIMIT 1")
    suspend fun getPreferencesDirect(): AppPreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(preferences: AppPreferencesEntity)
}
