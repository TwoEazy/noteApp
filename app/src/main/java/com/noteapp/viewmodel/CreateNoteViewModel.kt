package com.noteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.noteapp.data.model.Note
import com.noteapp.data.model.SessionManager
import com.noteapp.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class CreateNoteViewModel(
    private val repository: NoteRepository,
    private val sessionManager: SessionManager // Add SessionManager dependency
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateNoteUiState>(CreateNoteUiState.Idle)
    val uiState: StateFlow<CreateNoteUiState> = _uiState

    private val _currentNote = MutableStateFlow<Note?>(null)
    val currentNote: StateFlow<Note?> = _currentNote

    fun loadNote(noteId: Int) {
        if (noteId > 0) {
            viewModelScope.launch {
                _uiState.value = CreateNoteUiState.Loading
                val note = repository.getNoteById(noteId)
                _currentNote.value = note
                _uiState.value = CreateNoteUiState.Idle
            }
        }
    }

    fun saveNote(title: String, content: String) {
        viewModelScope.launch {
            _uiState.value = CreateNoteUiState.Saving

            val currentTime = System.currentTimeMillis()
            val userId = sessionManager.getUserId() // Get current user ID

            val note = _currentNote.value?.copy(
                title = title,
                content = content,
                updatedAt = currentTime,
                userId = userId // Ensure userId is set when updating
            ) ?: Note(
                title = title,
                content = content,
                createdAt = currentTime,
                updatedAt = currentTime,
                userId = userId // Set userId for new notes
            )

            try {
                if (_currentNote.value != null) {
                    repository.updateNote(note)
                } else {
                    repository.saveNote(note)
                }
                _uiState.value = CreateNoteUiState.Success
            } catch (e: Exception) {
                _uiState.value = CreateNoteUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class CreateNoteUiState {
    object Idle : CreateNoteUiState()
    object Loading : CreateNoteUiState()
    object Saving : CreateNoteUiState()
    object Success : CreateNoteUiState()
    data class Error(val message: String) : CreateNoteUiState()
}

class CreateNoteViewModelFactory(
    private val repository: NoteRepository,
    private val sessionManager: SessionManager // Add SessionManager dependency
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateNoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateNoteViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
