package com.noteapp.repository

import com.noteapp.data.local.dao.NoteDao
import com.noteapp.data.mapper.toDomainModel
import com.noteapp.data.mapper.toEntity
import com.noteapp.data.model.Note
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)?.toDomainModel()
    }

    suspend fun saveNote(note: Note) {
        noteDao.insertNote(note.toEntity())
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.toEntity())
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note.toEntity())
    }
}