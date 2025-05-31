package com.noteapp.data.remote.dto

data class UserDto(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val fullName: String? = null
)