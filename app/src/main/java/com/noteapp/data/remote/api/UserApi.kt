package com.noteapp.data.remote.api

import com.noteapp.data.remote.dto.LoginRequest
import com.noteapp.data.remote.dto.LoginResponse
import com.noteapp.data.remote.dto.RegisterRequest
import com.noteapp.data.remote.dto.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse
}