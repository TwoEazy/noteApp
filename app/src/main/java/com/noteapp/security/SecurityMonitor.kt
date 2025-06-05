package com.noteapp.security

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.View
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Updated SecurityMonitor with modern screenshot capture methods
 */
class SecurityMonitor(private val context: Context) {

    companion object {
        private const val TAG = "SecurityMonitor"
        private const val SCREENSHOT_DIR = "security_screenshots"
        private const val RANDOM_IMAGE_DIR = "security_random_images"
    }

    // Directory for storing security-related images
    private val securityDir: File by lazy {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "security_monitoring")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        dir
    }

    /**
     * Modern way to capture screenshots - works on all Android versions
     * @param activity The activity to capture
     * @return The file containing the screenshot, or null if capture failed
     */
    fun captureScreenshot(activity: Activity): File? {
        return try {
            // Method 1: Use View.draw() - most reliable
            val bitmap = captureUsingViewDraw(activity)

            if (bitmap != null) {
                saveScreenshotBitmap(bitmap)
            } else {
                Log.w(TAG, "Failed to capture screenshot using View.draw")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error capturing screenshot", e)
            null
        }
    }

    /**
     * Capture screenshot using View.draw() method - most compatible
     */
    private fun captureUsingViewDraw(activity: Activity): Bitmap? {
        return try {
            val rootView = activity.window.decorView.rootView

            // Create bitmap with view dimensions
            val bitmap = Bitmap.createBitmap(
                rootView.width,
                rootView.height,
                Bitmap.Config.ARGB_8888
            )

            // Create canvas and draw the view
            val canvas = Canvas(bitmap)
            rootView.draw(canvas)

            Log.d(TAG, "Screenshot captured using View.draw: ${rootView.width}x${rootView.height}")
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error in captureUsingViewDraw", e)
            null
        }
    }

    /**
     * Alternative method using PixelCopy (for API 24+)
     */
    private fun captureUsingPixelCopy(activity: Activity, callback: (Bitmap?) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                val rootView = activity.window.decorView.rootView
                val bitmap = Bitmap.createBitmap(
                    rootView.width,
                    rootView.height,
                    Bitmap.Config.ARGB_8888
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PixelCopy.request(
                        activity.window,
                        bitmap,
                        { result ->
                            if (result == PixelCopy.SUCCESS) {
                                Log.d(TAG, "Screenshot captured using PixelCopy")
                                callback(bitmap)
                            } else {
                                Log.e(TAG, "PixelCopy failed with result: $result")
                                callback(null)
                            }
                        },
                        Handler(Looper.getMainLooper())
                    )
                } else {
                    callback(null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in captureUsingPixelCopy", e)
                callback(null)
            }
        } else {
            callback(null)
        }
    }

    /**
     * Save bitmap to file
     */
    private fun saveScreenshotBitmap(bitmap: Bitmap): File? {
        return try {
            // Create timestamp for filename
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val filename = "screenshot_$timestamp.png"

            // Create directory if it doesn't exist
            val screenshotDir = File(securityDir, SCREENSHOT_DIR)
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs()
            }

            // Create file to save screenshot
            val file = File(screenshotDir, filename)

            // Save bitmap to file
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream) // Reduced quality for smaller files
            outputStream.flush()
            outputStream.close()

            Log.d(TAG, "Screenshot saved: ${file.absolutePath} (${file.length()} bytes)")
            file
        } catch (e: Exception) {
            Log.e(TAG, "Error saving screenshot", e)
            null
        }
    }

    /**
     * Create a simple test bitmap for testing uploads
     */
    fun createTestBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Fill with a solid color
        canvas.drawColor(android.graphics.Color.BLUE)

        // Draw some text
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 48f
            isAntiAlias = true
        }
        canvas.drawText("Test Screenshot", 50f, 150f, paint)

        return bitmap
    }

    /**
     * Converts a bitmap to a byte array
     * @param bitmap The bitmap to convert
     * @param format The format to use for compression
     * @param quality The quality of the compression (0-100)
     * @return The byte array containing the bitmap data
     */
    fun bitmapToByteArray(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 90): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(format, quality, outputStream)
        return outputStream.toByteArray()
    }

    /**
     * Schedules periodic security checks
     * @param intervalMinutes The interval between checks in minutes
     */
    fun scheduleSecurityChecks(intervalMinutes: Int) {
        // This would typically use WorkManager or AlarmManager to schedule periodic tasks
        // For now, we'll just log that it would be scheduled
        Log.d(TAG, "Security checks scheduled every $intervalMinutes minutes")
    }
}