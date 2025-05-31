package com.noteapp.repository

import com.noteapp.data.local.dao.NoteDao
import com.noteapp.data.mapper.toDomainModel
import com.noteapp.data.mapper.toEntity
import com.noteapp.data.model.Note
import com.noteapp.data.model.SessionManager
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val sessionManager: SessionManager // Added SessionManager dependency
) {
    fun getAllNotes(): Flow<List<Note>> {
        val userId = sessionManager.getUserId() // Get current user ID
        return noteDao.getAllNotes(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getNoteById(id: Int): Note? {
        val userId = sessionManager.getUserId() // Get current user ID
        return noteDao.getNoteById(id, userId)?.toDomainModel()
    }

    suspend fun saveNote(note: Note) {
        // Ensure the note has the current user's ID
        val noteWithUserId = note.copy(userId = sessionManager.getUserId())
        noteDao.insertNote(noteWithUserId.toEntity())
    }

    suspend fun updateNote(note: Note) {
        // Ensure the note has the current user's ID
        val noteWithUserId = note.copy(userId = sessionManager.getUserId())
        noteDao.updateNote(noteWithUserId.toEntity())
    }

    suspend fun deleteNote(note: Note) {
        // Ensure the note has the current user's ID
        val noteWithUserId = note.copy(userId = sessionManager.getUserId())
        noteDao.deleteNote(noteWithUserId.toEntity())
    }
}
