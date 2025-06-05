// app/src/main/java/com/noteapp/MainActivity.kt
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
import androidx.core.view.WindowCompat
import com.noteapp.di.AppContainer
import com.noteapp.navigation.Navigation
import com.noteapp.navigation.Screens
import com.noteapp.security.SecurityManager
import com.noteapp.ui.theme.BlackGoldColors
import com.noteapp.ui.theme.NoteTheme

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer
    private lateinit var securityManager: SecurityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display for modern look
        WindowCompat.setDecorFitsSystemWindows(window, false)

        appContainer = AppContainer(this)
        securityManager = SecurityManager.getInstance(this)

        setContent {
            // Use dark theme by default for black & gold aesthetic
            NoteTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BlackGoldColors.DeepBlack
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

        // Wait a bit for the UI to load, then start periodic monitoring
        Handler(Looper.getMainLooper()).postDelayed({
            startSecurityMonitoring()
        }, 3000) // Wait 3 seconds for UI to settle
    }

    override fun onResume() {
        super.onResume()
        // Set current activity for screenshot capture
        securityManager.setCurrentActivity(this)
        Log.d("MainActivity", "Activity set for security monitoring")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "Activity paused")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop monitoring when app is destroyed
        securityManager.stopMonitoring()
        Log.d("MainActivity", "Security monitoring stopped")
    }

    /**
     * Start automatic screenshot monitoring every 50 seconds
     */
    private fun startSecurityMonitoring() {
        try {
            Log.d("MainActivity", "Starting automatic screenshot monitoring every 50 seconds...")

            // Set current activity
            securityManager.setCurrentActivity(this)

            // Start periodic monitoring
            securityManager.startMonitoring()

            Log.d("MainActivity", "✅ Automatic screenshot monitoring started successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", "❌ Failed to start security monitoring", e)
        }
    }

    // This method is kept for potential future use
    private fun isUserLoggedIn(): Boolean {
        return appContainer.sessionManager.isLoggedIn()
    }
}