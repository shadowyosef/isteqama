package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AdhkarDao
import com.example.data.local.dao.QuranDataDao
import com.example.data.local.dao.RoutineDao
import com.example.data.local.dao.UserProgressDao
import com.example.data.local.entity.AdhkarEntity
import com.example.data.local.entity.QuranDataEntity
import com.example.data.local.entity.RoutineEntity
import com.example.data.local.entity.UserProgressEntity

import com.example.data.local.dao.AppPreferencesDao
import com.example.data.local.dao.ScreenLockDao
import com.example.data.local.entity.AppPreferencesEntity
import com.example.data.local.entity.ScreenLockEntity

@Database(
    entities = [
        RoutineEntity::class,
        QuranDataEntity::class,
        AdhkarEntity::class,
        UserProgressEntity::class,
        AppPreferencesEntity::class,
        ScreenLockEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun quranDataDao(): QuranDataDao
    abstract fun adhkarDao(): AdhkarDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun appPreferencesDao(): AppPreferencesDao
    abstract fun screenLockDao(): ScreenLockDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "istaqama_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
