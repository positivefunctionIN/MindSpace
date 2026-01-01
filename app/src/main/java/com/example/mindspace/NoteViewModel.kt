package com.example.mindspace

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindspace.data.local.Note
import com.example.mindspace.data.repository.NoteRepository
import com.example.mindspace.utils.ReminderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _selectedNote = MutableStateFlow<Note?>(null)
    val selectedNote: StateFlow<Note?> = _selectedNote.asStateFlow()

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
        viewModelScope.launch {
            val note = Note(
                title = title.ifBlank { "Untitled" },
                content = content,
                category = category
            )
            repository.insertNote(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch { repository.updateNote(note) }
    }

    fun deleteNote(noteId: Int) {
        viewModelScope.launch { repository.moveToTrash(noteId) }
    }

    fun loadNoteById(noteId: Int) {
        viewModelScope.launch {
            _selectedNote.value = repository.getNoteById(noteId)
        }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch { repository.togglePin(note.id, !note.isPinned) }
    }

    fun toggleFavorite(note: Note) {
        viewModelScope.launch { repository.toggleFavorite(note.id, !note.isFavorite) }
    }

    fun setReminder(noteId: Int, reminderTime: Long) {
        viewModelScope.launch {
            repository.setReminder(noteId, reminderTime)
            ReminderManager.scheduleReminder(getApplication(), noteId, reminderTime)
        }
    }

    fun cancelReminder(noteId: Int) {
        viewModelScope.launch {
            repository.cancelReminder(noteId)
            ReminderManager.cancelReminder(getApplication(), noteId)
        }
    }
}
