package com.noteapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.noteapp.di.AppContainer
import com.noteapp.navigation.Navigation
import com.noteapp.navigation.Screens
import com.noteapp.security.SecurityManager
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

        // Wait a bit for the UI to load, then run tests
        Handler(Looper.getMainLooper()).postDelayed({
            runSecurityTests()
        }, 2000) // Wait 2 seconds for UI to settle
    }

    /**
     * Run multiple security tests to debug issues
     */
    private fun runSecurityTests() {
        val securityManager = SecurityManager.getInstance(this)
        // Test 2: Full security check after a delay
        Handler(Looper.getMainLooper()).postDelayed({
            testFullSecurityCheck(securityManager)
        }, 3000)

    }

    /**
     * Test 2: Full security check
     */
    private fun testFullSecurityCheck(securityManager: SecurityManager) {
        try {
            Log.d("MainActivity", "Running full security check...")
            securityManager.triggerSecurityCheck(this)
        } catch (e: Exception) {
            Log.e("MainActivity", "Full security check failed", e)
        }
    }
    // This method is kept for potential future use
    private fun isUserLoggedIn(): Boolean {
        return appContainer.sessionManager.isLoggedIn()
    }
}