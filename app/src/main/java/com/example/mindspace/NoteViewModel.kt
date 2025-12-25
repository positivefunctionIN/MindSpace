package com.example.mindspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mindspace.data.local.Note
import com.example.mindspace.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _selectedNote = MutableStateFlow<Note?>(null)
    val selectedNote: StateFlow<Note?> = _selectedNote.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessages = MutableStateFlow<List<String>>(emptyList())
    val errorMessages: StateFlow<List<String>> = _errorMessages.asStateFlow()

    val trashCount: StateFlow<Int> = repository.trashCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            repository.allActiveNotes.collect { notes ->
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
            } catch (e: Exception) {
                _errorMessages.value = _errorMessages.value + "Error adding note: ${e.message}"
            }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            try {
                repository.updateNote(note)
            } catch (e: Exception) {
                _errorMessages.value = _errorMessages.value + "Error updating note: ${e.message}"
            }
        }
    }

    fun deleteNote(noteId: Int) {
        viewModelScope.launch {
            try {
                repository.moveToTrash(noteId)
            } catch (e: Exception) {
                _errorMessages.value = _errorMessages.value + "Error deleting note: ${e.message}"
            }
        }
    }

    fun loadNoteById(noteId: Int) {
        viewModelScope.launch {
            try {
                val note = repository.getNoteById(noteId)
                _selectedNote.value = note
            } catch (e: Exception) {
                _errorMessages.value = _errorMessages.value + "Error loading note: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessages.value = emptyList()
    }
}

class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
