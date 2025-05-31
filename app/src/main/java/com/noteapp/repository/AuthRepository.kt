package com.noteapp.repository

import com.noteapp.data.mapper.toDomainModel
import com.noteapp.data.model.User
import com.noteapp.data.remote.RetrofitClient
import com.noteapp.data.remote.dto.LoginRequest
import com.noteapp.data.remote.dto.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {
    private val api = RetrofitClient.userApi

    suspend fun login(email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(email = email, password = password)
                val response = api.login(loginRequest)

                if (response.success && response.data != null) {
                    val user = response.data.toDomainModel()
                    Result.success(user)
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Login failed: ${e.message}"))
            }
        }
    }

    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val registerRequest = RegisterRequest(
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName
                )
                val response = api.register(registerRequest)

                if (response.success && response.data != null) {
                    val user = response.data.toDomainModel()
                    Result.success(user)
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Registration failed: ${e.message}"))
            }
        }
    }
}