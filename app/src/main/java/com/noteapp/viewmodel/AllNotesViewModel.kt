package com.noteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.noteapp.data.model.Note
import com.noteapp.data.model.SessionManager
import com.noteapp.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AllNotesViewModel(
    private val repository: NoteRepository,
    private val sessionManager: SessionManager // Add SessionManager dependency
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    private val _uiState = MutableStateFlow<AllNotesUiState>(AllNotesUiState.Loading)
    val uiState: StateFlow<AllNotesUiState> = _uiState

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            // Check if user is logged in
            if (!isUserLoggedIn()) {
                _uiState.value = AllNotesUiState.Empty
                return@launch
            }

            _uiState.value = AllNotesUiState.Loading
            repository.getAllNotes().collectLatest { notesList ->
                _notes.value = notesList
                _uiState.value = if (notesList.isEmpty()) {
                    AllNotesUiState.Empty
                } else {
                    AllNotesUiState.Success
                }
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            if (isUserLoggedIn()) {
                repository.deleteNote(note)
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        return sessionManager.isLoggedIn() && sessionManager.getUserId() != -1
    }

    fun logout() {
        sessionManager.clearSession()
        _notes.value = emptyList()
        _uiState.value = AllNotesUiState.Empty
    }
}

sealed class AllNotesUiState {
    object Loading : AllNotesUiState()
    object Success : AllNotesUiState()
    object Empty : AllNotesUiState()
}

class AllNotesViewModelFactory(
    private val repository: NoteRepository,
    private val sessionManager: SessionManager // Add SessionManager dependency
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllNotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AllNotesViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}