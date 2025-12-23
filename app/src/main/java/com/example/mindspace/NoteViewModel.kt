package com.example.mindspace

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindspace.data.local.Note
import com.example.mindspace.data.local.NoteDatabase
import com.example.mindspace.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _selectedNote = MutableStateFlow<Note?>(null)
    val selectedNote: StateFlow<Note?> = _selectedNote.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessages = MutableStateFlow<List<String>>(emptyList())
    val errorMessages: StateFlow<List<String>> = _errorMessages.asStateFlow()

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            repository.allNotes.collect { notes ->
                _notes.value = notes
            }
        }
    }

    fun addNote(title: String, content: String, category: String = "General") {
        if (title.isBlank() && content.isBlank()) {
            _errorMessages.value = _errorMessages.value + "Title and content cannot be blank"
            return
        }
        viewModelScope.launch {
            try {
                val note = Note(
                    title = title.ifBlank { "Untitled" },
                    content = content,
                    category = category
                )
                repository.insertNote(note)
            }
            catch (e: Exception) {
                _errorMessages.value = _errorMessages.value + "Error adding note: ${e.message}"
            }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            try {
                repository.updateNote(note)
            }
            catch (e: Exception) {
                _errorMessages.value = _errorMessages.value + "Error updating note: ${e.message}"
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            try {
                repository.deleteNote(note)
            }
            catch (e: Exception) {
                _errorMessages.value = _errorMessages.value + "Error deleting note: ${e.message}"
            }
        }
    }

    fun loadNoteById(noteId: Int) {
        viewModelScope.launch {
            try {
                val note = repository.getNoteById(noteId)
                _selectedNote.value = note
            }
            catch (e: Exception) {
                _errorMessages.value = _errorMessages.value + "Error loading note: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessages.value = emptyList()
    }
}
