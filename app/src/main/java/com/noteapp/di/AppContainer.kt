package com.noteapp.di

import android.content.Context
import androidx.room.Room
import com.noteapp.data.local.AppDatabase
import com.noteapp.data.model.SessionManager
import com.noteapp.repository.AuthRepository
import com.noteapp.repository.NoteRepository
import com.noteapp.viewmodel.LoginViewModel
import com.noteapp.viewmodel.RegisterViewModel

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "note_database"
    ).build()

    // Create DAOs
    private val noteDao = database.noteDao()

    // Create repositories
    val noteRepository = NoteRepository(noteDao)
    val authRepository = AuthRepository() // todo
    val sessionManager = SessionManager(context)  // todo

    // Create ViewModels
    val loginViewModel = LoginViewModel(authRepository, sessionManager)
    val registerViewModel = RegisterViewModel(authRepository)
}