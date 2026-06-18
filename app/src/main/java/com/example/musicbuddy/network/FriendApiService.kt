package com.example.musicbuddy.network

import com.example.musicbuddy.data.models.*
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * AuthApiService - Retrofit interface for authentication endpoints
 * Communicates with Node.js backend
 */
interface FriendApiService {

    /**
     * Send a friend request (setting it to PENDING)
     * POST /api/auth/sendFriendRequest
     */
    @POST("api/auth/sendFriendRequest")
    suspend fun sendFriendRequest(@Body request: FriendRequestField): GenericFriendResponse
}