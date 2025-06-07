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
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateNoteUiState>(CreateNoteUiState.Idle)
    val uiState: StateFlow<CreateNoteUiState> = _uiState

    private val _currentNote = MutableStateFlow<Note?>(null)
    val currentNote: StateFlow<Note?> = _currentNote

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    fun loadNote(noteId: Int) {
        if (!isUserLoggedIn()) {
            _authError.value = "User not authenticated"
            return
        }

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
        if (!isUserLoggedIn()) {
            _uiState.value = CreateNoteUiState.Error("User not authenticated. Please log in again.")
            return
        }

        if (title.isBlank() && content.isBlank()) {
            _uiState.value = CreateNoteUiState.Error("Note cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = CreateNoteUiState.Saving

            val currentTime = System.currentTimeMillis()
            val userId = sessionManager.getUserId()

            // Double-check user ID is valid
            if (userId == -1) {
                _uiState.value = CreateNoteUiState.Error("Invalid user session. Please log in again.")
                return@launch
            }

            val note = _currentNote.value?.copy(
                title = title,
                content = content,
                updatedAt = currentTime,
                userId = userId
            ) ?: Note(
                title = title,
                content = content,
                createdAt = currentTime,
                updatedAt = currentTime,
                userId = userId
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

    private fun isUserLoggedIn(): Boolean {
        return sessionManager.isLoggedIn() && sessionManager.getUserId() != -1
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
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateNoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateNoteViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}