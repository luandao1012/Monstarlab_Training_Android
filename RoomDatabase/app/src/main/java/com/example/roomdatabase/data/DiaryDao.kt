package com.example.roomdatabase.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary ORDER BY date DESC")
    fun getFlowAllDiary(): Flow<List<Diary>>

    @Query("SELECT * FROM diary ORDER BY date DESC")
    fun getListAllDiary(): List<Diary>

    @Query("SELECT * FROM diary WHERE date >= :firstDate AND date <= :lastDate ORDER BY date")
    fun getDiaryInMonth(firstDate: Long, lastDate: Long): Flow<List<Diary>>

    @Query("SELECT * FROM diary WHERE content LIKE '%'||:word||'%'")
    fun search(word: String): List<Diary>

    @Query("SELECT * FROM diary WHERE date = :date")
    suspend fun getDiary(date: Long): Diary?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDiary(diary: Diary)

    @Update
    suspend fun updateDiary(diary: Diary)

    @Delete
    suspend fun deleteDiary(diary: Diary)
}