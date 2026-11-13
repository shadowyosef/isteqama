package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.QuranDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDataDao {
    @Query("SELECT * FROM quran_data WHERE surahNumber = :surahNumber ORDER BY ayahNumberInSurah ASC")
    fun getAyahsForSurah(surahNumber: Int): Flow<List<QuranDataEntity>>

    @Query("SELECT * FROM quran_data WHERE page = :page ORDER BY globalAyahNumber ASC")
    fun getAyahsForPage(page: Int): Flow<List<QuranDataEntity>>

    @Query("SELECT * FROM quran_data WHERE surahNumber = :surah AND ayahNumberInSurah = :ayah LIMIT 1")
    suspend fun getAyah(surah: Int, ayah: Int): QuranDataEntity?

    @Query("SELECT * FROM quran_data WHERE isBookmarked = 1")
    fun getBookmarks(): Flow<List<QuranDataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(verses: List<QuranDataEntity>)

    @Query("UPDATE quran_data SET isBookmarked = :bookmarked WHERE globalAyahNumber = :globalNumber")
    suspend fun updateBookmark(globalNumber: Int, bookmarked: Boolean)
}
