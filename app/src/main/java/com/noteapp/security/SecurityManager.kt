package com.noteapp.security

import android.content.Context
import android.util.Log

/**
 * SecurityManager for pure screenshot monitoring only
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
        Log.d(TAG, "Initializing SecurityManager for pure screenshot monitoring")
        securityMonitoringService.initialize()
    }

    /**
     * Starts the screenshot monitoring service with default settings
     */
    fun startMonitoring() {
        Log.d(TAG, "Starting screenshot monitoring")
        securityMonitoringService.schedulePeriodicChecks()
    }

    /**
     * Starts the screenshot monitoring service with custom screenshot interval
     * @param screenshotIntervalMinutes The interval between screenshot captures in minutes
     */
    fun startMonitoring(screenshotIntervalMinutes: Int) {
        Log.d(TAG, "Starting screenshot monitoring with custom interval: $screenshotIntervalMinutes minutes")
        securityMonitoringService.schedulePeriodicChecks(screenshotIntervalMinutes)
    }

    /**
     * Stops the screenshot monitoring service
     */
    fun stopMonitoring() {
        Log.d(TAG, "Stopping screenshot monitoring")
        securityMonitoringService.stopPeriodicChecks()
    }

    /**
     * Triggers an immediate screenshot capture and upload
     * @param activity The current activity
     */
    fun triggerSecurityCheck(activity: android.app.Activity) {
        Log.d(TAG, "Triggering immediate screenshot capture")
        securityMonitoringService.performSecurityCheck(activity)
    }
}