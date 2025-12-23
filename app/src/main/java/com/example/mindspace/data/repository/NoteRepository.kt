package com.example.mindspace.data.repository

import com.example.mindspace.data.local.Note
import com.example.mindspace.data.local.NoteDao
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    suspend fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)
    }

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }

    fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query)
    }

    fun getNotesByCategory(category: String): Flow<List<Note>> {
        return noteDao.getNotesByCategory(category)
    }
}
