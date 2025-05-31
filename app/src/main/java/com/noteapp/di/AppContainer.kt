package com.noteapp.di

import android.content.Context
import androidx.room.Room
import com.noteapp.data.local.AppDatabase
import com.noteapp.data.model.SessionManager
import com.noteapp.repository.AuthRepository
import com.noteapp.repository.NoteRepository
import com.noteapp.viewmodel.CreateNoteViewModel
import com.noteapp.viewmodel.CreateNoteViewModelFactory
import com.noteapp.viewmodel.LoginViewModel
import com.noteapp.viewmodel.RegisterViewModel

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "note_database"
    )
        .addMigrations(AppDatabase.MIGRATION_1_2) // Add migration
        .build()

    // Create DAOs
    private val noteDao = database.noteDao()

    // Create repositories
    val sessionManager = SessionManager(context)
    val noteRepository = NoteRepository(noteDao, sessionManager) // Pass sessionManager
    val authRepository = AuthRepository()

    // Create ViewModels
    val loginViewModel = LoginViewModel(authRepository, sessionManager)
    val registerViewModel = RegisterViewModel(authRepository)

    // Create ViewModel Factory for CreateNoteViewModel
    val createNoteViewModelFactory = CreateNoteViewModelFactory(noteRepository, sessionManager)
}
