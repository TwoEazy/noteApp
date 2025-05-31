package com.noteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
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
                    // Always start with LoginScreen
                    val startDestination = Screens.Login.route

                    Navigation(
                        appContainer = appContainer,
                        startDestination = startDestination
                    )
                }
            }
        }
    }

    // This method is kept for potential future use
    private fun isUserLoggedIn(): Boolean {
        return appContainer.sessionManager.isLoggedIn()
    }
}
