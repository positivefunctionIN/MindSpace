package com.example.mindspace.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    // ===== READ OPERATIONS =====

    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllActiveNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): Note?

    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isDeleted = 0 AND category = :category ORDER BY updatedAt DESC")
    fun getNotesByCategory(category: String): Flow<List<Note>>

    @Query("""
        SELECT * FROM notes 
        WHERE isDeleted = 0 
        AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%')
        ORDER BY updatedAt DESC
    """)
    fun searchNotes(query: String): Flow<List<Note>>

    // ===== CRUD OPERATIONS =====

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    // ===== TRASH OPERATIONS =====

    @Query("SELECT * FROM notes WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getTrashNotes(): Flow<List<Note>>

    @Query("SELECT COUNT(*) FROM notes WHERE isDeleted = 1")
    fun getTrashCount(): Flow<Int>

    // Soft Delete: Move to Trash
    @Query("UPDATE notes SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :noteId")
    suspend fun moveToTrash(noteId: Int, deletedAt: Long)

    // Restore from Trash
    @Query("UPDATE notes SET isDeleted = 0, deletedAt = null WHERE id = :noteId")
    suspend fun restoreFromTrash(noteId: Int)

    // Permanently Delete a single note
    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deletePermanently(noteId: Int)

    // Empty Entire Trash
    @Query("DELETE FROM notes WHERE isDeleted = 1")
    suspend fun emptyTrash()

    // Auto-delete notes from trash older than a certain time
    @Query("DELETE FROM notes WHERE isDeleted = 1 AND deletedAt < :threshold")
    suspend fun deleteOldTrashNotes(threshold: Long)
}