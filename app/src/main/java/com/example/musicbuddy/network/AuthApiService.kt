package com.example.musicbuddy.network

import com.example.musicbuddy.data.models.LoginRequest
import com.example.musicbuddy.data.models.LoginResponse
import com.example.musicbuddy.data.models.RegisterRequest
import com.example.musicbuddy.data.models.RegisterResponse
import com.example.musicbuddy.data.models.UpdateFieldResponse
import com.example.musicbuddy.data.models.UpdateFieldRequest
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * AuthApiService - Retrofit interface for authentication endpoints
 * Communicates with Node.js backend
 */
interface AuthApiService {

    /**
     * Register a new user
     * POST /api/auth/register
     */
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    /**
     * Login user and get JWT token
     * POST /api/auth/login
     */
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    /**
     * Update the field of user
     * POST /api/auth/updateFieldUser
     */
    @POST("api/auth/updateFieldUser")
    suspend fun updateFieldUser(@Body request: UpdateFieldRequest): UpdateFieldResponse
}