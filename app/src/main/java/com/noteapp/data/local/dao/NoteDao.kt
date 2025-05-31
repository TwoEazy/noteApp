package com.noteapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.noteapp.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE user_id = :userId ORDER BY updated_at DESC")
    fun getAllNotes(userId: Int): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id AND user_id = :userId")
    suspend fun getNoteById(id: Int, userId: Int): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE user_id = :userId")
    suspend fun deleteAllNotes(userId: Int)
}
