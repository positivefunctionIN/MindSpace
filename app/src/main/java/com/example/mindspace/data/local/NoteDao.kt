package com.example.mindspace.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")  //Sort by most recent
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")  //Get note by ID
    suspend fun getNoteById(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)  //Insert
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)   //Update

    @Delete
    suspend fun deleteNote(note: Note)   //Delete

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()  //Delete all notes

    @Query("SELECT * FROM notes" +
            " WHERE title LIKE '%' || :query || '%' " +
            "OR content LIKE '%' || :query || '%'" +
            "ORDER BY timestamp DESC")
    fun searchNotes(query: String): Flow<List<Note>>  //Search notes by title or content

    @Query("SELECT * FROM notes WHERE category = :category ORDER BY timestamp DESC")
    fun getNotesByCategory(category: String): Flow<List<Note>>  //Get notes by category

}
