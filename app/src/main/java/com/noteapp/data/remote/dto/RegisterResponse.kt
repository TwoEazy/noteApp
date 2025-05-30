package com.noteapp.data.remote.dto

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: UserDto? = null
)