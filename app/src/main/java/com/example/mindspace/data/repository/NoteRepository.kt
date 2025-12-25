package com.example.mindspace.data.repository

import com.example.mindspace.data.local.Note
import com.example.mindspace.data.local.NoteDao
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    // ===== READ OPERATIONS =====

    val allActiveNotes: Flow<List<Note>> = noteDao.getAllActiveNotes()

    val favoriteNotes: Flow<List<Note>> = noteDao.getFavoriteNotes()

    fun getNotesByCategory(category: String): Flow<List<Note>> = noteDao.getNotesByCategory(category)

    fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)

    suspend fun getNoteById(id: Int): Note? = noteDao.getNoteById(id)

    // ===== CRUD OPERATIONS =====

    suspend fun insertNote(note: Note) = noteDao.insertNote(note)

    suspend fun updateNote(note: Note) = noteDao.updateNote(
        note.copy(updatedAt = System.currentTimeMillis())
    )

    // ===== TRASH OPERATIONS =====

    val trashNotes: Flow<List<Note>> = noteDao.getTrashNotes()

    val trashCount: Flow<Int> = noteDao.getTrashCount()

    suspend fun moveToTrash(noteId: Int) = noteDao.moveToTrash(noteId, System.currentTimeMillis())

    suspend fun restoreFromTrash(noteId: Int) = noteDao.restoreFromTrash(noteId)

    suspend fun deletePermanently(noteId: Int) = noteDao.deletePermanently(noteId)

    suspend fun emptyTrash() = noteDao.emptyTrash()

    /**
     * Deletes notes from the trash that are older than 30 days.
     * Call this on app start to perform cleanup.
     */
    suspend fun cleanOldTrash() {
        val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
        noteDao.deleteOldTrashNotes(thirtyDaysAgo)
    }
}