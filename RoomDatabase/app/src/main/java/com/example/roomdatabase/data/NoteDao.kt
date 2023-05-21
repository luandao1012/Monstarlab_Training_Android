package com.example.roomdatabase.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note ORDER BY date DESC")
    fun getFlowAllNote(): Flow<List<Note>>

    @Query("SELECT * FROM note ORDER BY date DESC")
    fun getListAllNote(): List<Note>

    @Query("SELECT * FROM note WHERE date >= :firstDate AND date <= :lastDate ORDER BY date")
    fun getNoteInMonth(firstDate: Long, lastDate: Long): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE content LIKE '%'||:word||'%' and type =:type")
    fun search(word: String, type: Type): List<Note>

    @Query("SELECT * FROM note WHERE date = :date")
    suspend fun getNote(date: Long): Note?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM note WHERE date = :date")
    fun getAllNoteByDate(date: Long): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE type = :type")
    fun getAllNoteByType(type: Type): Flow<List<Note>>
}