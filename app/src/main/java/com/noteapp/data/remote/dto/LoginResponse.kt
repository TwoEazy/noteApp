package com.noteapp.data.remote.dto

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: UserDto? = null,
    val token: String? = null
)