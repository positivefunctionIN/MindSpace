package com.example.mindspace.ui.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindspace.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel(){
    val trashNotes = noteRepository.trashNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val trashCount = noteRepository.trashCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun restoreNote(noteId: Int) = viewModelScope.launch {
        noteRepository.restoreFromTrash(noteId)
    }

    fun deletePermanently(noteId: Int) = viewModelScope.launch {
        noteRepository.deletePermanently(noteId)
    }

    fun emptyTrash() = viewModelScope.launch {
        noteRepository.emptyTrash()
    }

    init {
        viewModelScope.launch {
            noteRepository.cleanOldTrash()
        }
    }
}
