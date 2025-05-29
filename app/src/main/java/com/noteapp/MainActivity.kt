package com.example.note

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.noteapp.di.AppContainer
import com.noteapp.navigation.Navigation
import com.noteapp.navigation.Screens
import com.noteapp.ui.theme.NoteTheme

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContainer = AppContainer(this)

        setContent {
            NoteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Always start with AllNotesScreen
                    val startDestination = Screens.AllNotes.route

                    Navigation(
                        appContainer = appContainer,
                        startDestination = startDestination
                    )
                }
            }
        }
    }

    // This method is no longer used for determining start destination
    // but kept for other potential uses
    private fun isUserLoggedIn(): Boolean {
        // Check SharedPreferences or SessionManager
        return appContainer.sessionManager.isLoggedIn()
    }
}