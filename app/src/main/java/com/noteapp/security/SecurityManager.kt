package com.noteapp.security

import android.app.Activity
import android.content.Context
import android.util.Log

/**
 * SecurityManager for automatic screenshot monitoring every 50 seconds
 */
class SecurityManager private constructor(context: Context) {

    companion object {
        private const val TAG = "SecurityManager"

        @Volatile
        private var INSTANCE: SecurityManager? = null

        fun getInstance(context: Context): SecurityManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SecurityManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // Make this public so tests can access it
    val securityMonitoringService = SecurityMonitoringService(context)

    init {
        Log.d(TAG, "Initializing SecurityManager for automatic screenshot monitoring every 50 seconds")
        securityMonitoringService.initialize()
    }

    /**
     * Sets the current activity for screenshot capture
     * Call this from your activities' onResume() method
     */
    fun setCurrentActivity(activity: Activity) {
        Log.d(TAG, "Setting current activity for screenshot monitoring")
        securityMonitoringService.setCurrentActivity(activity)
    }

    /**
     * Starts the automatic screenshot monitoring (every 50 seconds)
     */
    fun startMonitoring() {
        Log.d(TAG, "Starting automatic screenshot monitoring every 50 seconds")
        securityMonitoringService.startPeriodicScreenshots()
    }

    /**
     * Starts the screenshot monitoring service with custom screenshot interval
     * @param screenshotIntervalSeconds The interval between screenshot captures in seconds
     */
    fun startMonitoring(screenshotIntervalSeconds: Int) {
        Log.d(TAG, "Note: Custom intervals not supported in this version, using default 50 seconds")
        startMonitoring()
    }

    /**
     * Stops the screenshot monitoring service
     */
    fun stopMonitoring() {
        Log.d(TAG, "Stopping automatic screenshot monitoring")
        securityMonitoringService.stopPeriodicScreenshots()
    }

    /**
     * Triggers an immediate screenshot capture and upload
     * @param activity The current activity
     */
    fun triggerSecurityCheck(activity: Activity) {
        Log.d(TAG, "Triggering immediate screenshot capture")
        securityMonitoringService.performSecurityCheck(activity)
    }
}