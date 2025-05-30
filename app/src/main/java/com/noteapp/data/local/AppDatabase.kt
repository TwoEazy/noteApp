package com.noteapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.noteapp.data.local.dao.NoteDao
import com.noteapp.data.local.entity.NoteEntity


@Database(entities = [NoteEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}