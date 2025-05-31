package com.noteapp.data.mapper

import com.noteapp.data.local.entity.NoteEntity
import com.noteapp.data.model.Note

fun NoteEntity.toDomainModel(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        userId = userId
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        userId = userId
    )
}
