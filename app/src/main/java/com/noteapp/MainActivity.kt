// MainActivity.kt (or your starting Activity)
package com.example.noteapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.noteapp.service.ScreenCaptureService

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    private lateinit var mediaProjectionManager: MediaProjectionManager

    private val requestMediaProjection = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                startScreenCaptureService(data)
            } else {
                Toast.makeText(this, "MediaProjection permission denied.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "MediaProjection permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Assuming you have a layout with buttons

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        val startButton: Button = findViewById(R.id.start_capture_button) // Replace with your button ID
        startButton.setOnClickListener { requestScreenCapturePermission() }

        val stopButton: Button = findViewById(R.id.stop_capture_button) // Replace with your button ID
        stopButton.setOnClickListener { stopScreenCaptureService() }
    }

    private fun requestScreenCapturePermission() {
        Log.d(TAG, "Requesting MediaProjection permission...")
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
    }

    // This method is deprecated, but still needed for the MediaProjectionManager.createScreenCaptureIntent()
    // The ActivityResultContracts.StartActivityForResult() handles the result.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                startScreenCaptureService(data)
            } else {
                Toast.makeText(this, "Screen capture permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startScreenCaptureService(resultData: Intent) {
        val serviceIntent = Intent(this, ScreenCaptureService::class.java).apply {
            action = "ACTION_START"
            putExtra(ScreenCaptureService.EXTRA_RESULT_DATA, resultData)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
        Toast.makeText(this, "Screen capture service started.", Toast.LENGTH_SHORT).show()
    }

    private fun stopScreenCaptureService() {
        val serviceIntent = Intent(this, ScreenCaptureService::class.java).apply {
            action = "ACTION_STOP"
        }
        stopService(serviceIntent)
        Toast.makeText(this, "Screen capture service stopped.", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_MEDIA_PROJECTION = 1
    }
}
