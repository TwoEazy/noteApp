package com.noteapp.data.mapper

import com.noteapp.data.local.entity.NoteEntity
import com.noteapp.data.model.Note

fun NoteEntity.toDomainModel(): Note {
    return Note(
        id = this.id,
        title = this.title,
        content = this.content,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = this.id,
        title = this.title,
        content = this.content,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}


