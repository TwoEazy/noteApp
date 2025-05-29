package com.noteapp.repository

import com.noteapp.data.model.User


class AuthRepository {
    // Mock implementation - replace with real API calls
    suspend fun login(email: String, password: String): Result<User> {

        // Mock validation
        return if (email == "test@test.com" && password == "password") {
            Result.success(User(id = 1, email = email, name = "Test User"))
        } else {
            Result.failure(Exception("Invalid credentials"))
        }
    }

    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Result<User> {

        // Mock registration - always succeeds for demo
        return Result.success(
            User(
                id = 1,
                email = email,
                name = "$firstName $lastName"
            )
        )
    }
}
