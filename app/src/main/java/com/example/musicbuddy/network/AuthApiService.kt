package com.example.musicbuddy.network

import com.example.musicbuddy.data.models.*
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT

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
    @PATCH("api/auth/updateFieldUser")    //per l'aggiornamento dei campi si usa PUT o PATCH in HTTP
    suspend fun updateFieldUser(@Body request: UpdateFieldRequest): UpdateFieldResponse

    /**
     * Retrieve all users infos
     * GET /api/auth/getAllUsersInfos
     */
    @GET("api/auth/getAllUsersInfos")
    suspend fun getAllUsersInfos(): GetAllUsersResponse<List<UserInfos>>

    /**
     * Delete a user
     * DELETE /api/auth/deleteUser
     */
    @DELETE("api/auth/deleteUser")
    suspend fun deleteUser(@Body request: DeleteUserRequest): DeleteUserResponse
}