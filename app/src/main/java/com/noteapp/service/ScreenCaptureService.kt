// ScreenCaptureService.kt
package com.example.noteapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.example.noteapp.R // Assuming you have an R file for resources
import com.noteapp.security.SupabaseUploader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.app.Activity // Added for Activity.RESULT_OK

class ScreenCaptureService : Service() {

    private val TAG = "ScreenCaptureService"
    private val NOTIFICATION_CHANNEL_ID = "ScreenCaptureChannel"
    private val NOTIFICATION_ID = 101

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var screenDensity: Int = 0

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private lateinit var supabaseUploader: SupabaseUploader

    companion object {
        var sMediaProjection: MediaProjection? = null
        var sResultData: Intent? = null
    }

    override fun onCreate() {
        super.onCreate()
        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        supabaseUploader = SupabaseUploader(applicationContext)
        startForegroundServiceNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            Log.e(TAG, "onStartCommand: Intent is null, stopping service.")
            stopSelf()
            return START_NOT_STICKY
        }

        val action = intent.action
        if (action == "ACTION_START") {
            sResultData = intent.getParcelableExtra(EXTRA_RESULT_DATA)
            // Use the static sResultData and sMediaProjection from MainActivity
            sMediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, sResultData!!)
            if (sMediaProjection == null) {
                Log.e(TAG, "MediaProjection is null, permission not granted.")
                stopSelf()
                return START_NOT_STICKY
            }
            startScreenCapture()
        } else if (action == "ACTION_STOP") {
            stopScreenCapture()
            stopSelf()
        }

        return START_STICKY
    }

    private fun startForegroundServiceNotification() {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Screen Capture Running")
            .setContentText("Capturing screen in the background.")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app's icon
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Screen Capture Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun startScreenCapture() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
        screenDensity = metrics.densityDpi

        imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2)
        sMediaProjection?.registerCallback(MediaProjectionCallback(), Handler(Looper.getMainLooper()))

        virtualDisplay = sMediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            screenWidth,
            screenHeight,
            screenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null,
            null
        )

        Log.d(TAG, "Screen capture started. Resolution: ${screenWidth}x${screenHeight}")
        startPeriodicCapture()
    }

    private fun startPeriodicCapture() {
        serviceScope.launch {
            while (true) {
                delay(5000) // Capture every 5 seconds
                captureScreenshot()
            }
        }
    }

    private fun captureScreenshot() {
        var image: Image? = null
        try {
            image = imageReader?.acquireLatestImage()
            if (image != null) {
                val planes = image.planes
                val buffer: ByteBuffer = planes[0].buffer
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * screenWidth

                val bitmap = Bitmap.createBitmap(screenWidth + rowPadding / pixelStride, screenHeight, Bitmap.Config.ARGB_8888)
                bitmap.copyPixelsFromBuffer(buffer)

                // Crop the bitmap to the actual screen width (remove padding)
                val croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight)
                bitmap.recycle() // Recycle original bitmap

                saveBitmapToFileAndUpload(croppedBitmap)
                croppedBitmap.recycle()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error capturing screenshot: ${e.message}", e)
        } finally {
            image?.close()
        }
    }

    private suspend fun saveBitmapToFileAndUpload(bitmap: Bitmap) {
        val filename = "screenshot_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.png"
        val imagesDir = File(applicationContext.cacheDir, "screenshots")
        if (!imagesDir.exists()) imagesDir.mkdirs()
        val file = File(imagesDir, filename)

        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            }
            Log.d(TAG, "Screenshot saved locally: ${file.absolutePath}")
            supabaseUploader.uploadFile(file, "screenshots")
        } catch (e: IOException) {
            Log.e(TAG, "Error saving bitmap to file: ${e.message}", e)
        }
    }

    private fun stopScreenCapture() {
        serviceJob.cancel() // Cancel the coroutine for periodic capture
        sMediaProjection?.stop()
        virtualDisplay?.release()
        imageReader?.close()
        sMediaProjection = null
        sResultData = null
        Log.d(TAG, "Screen capture stopped.")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopScreenCapture()
        Log.d(TAG, "ScreenCaptureService destroyed.")
    }

    inner class MediaProjectionCallback : MediaProjection.Callback() {
        override fun onStop() {
            super.onStop()
            Log.e(TAG, "MediaProjection stopped unexpectedly.")
            stopScreenCapture()
            stopSelf()
        }
    }

    companion object {
        const val EXTRA_RESULT_DATA = "extra_result_data"
    }
}
