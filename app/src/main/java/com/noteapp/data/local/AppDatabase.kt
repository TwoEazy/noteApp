package com.noteapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.noteapp.data.local.dao.NoteDao
import com.noteapp.data.local.entity.NoteEntity

// Update version from 1 to 2
@Database(entities = [NoteEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        // Define migration from version 1 to 2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add user_id column with default value of -1
                // This ensures existing notes won't break, but they won't be visible to any user
                database.execSQL("ALTER TABLE notes ADD COLUMN user_id INTEGER NOT NULL DEFAULT -1")
            }
        }
    }
}
