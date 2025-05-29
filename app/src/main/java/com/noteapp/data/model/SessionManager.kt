package com.noteapp.data.model

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings.Global.putInt
import android.provider.Settings.Global.putString

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(userId: Int, email: String) {
        prefs.edit().apply {
            putInt("user_id", userId)
            putString("user_email", email)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun getUserId(): Int = prefs.getInt("user_id", -1)

    fun getUserEmail(): String? = prefs.getString("user_email", null)
}
