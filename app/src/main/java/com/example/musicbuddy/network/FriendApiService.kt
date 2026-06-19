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

    @POST("api/auth/getAllFriends")
    suspend fun getAllFriends(@Body request: GetAllFriendsRequest): GetAllFriendsResponse

    @PATCH("api/auth/acceptFriendRequest")
    suspend fun acceptFriendRequest(@Body request: FriendRequestField): GenericFriendResponse

    //@DELETE("api/auth/rejectFriendRequest")
    //suspend fun rejectFriendRequest(@Body request: FriendRequestField): GenericFriendResponse

    //Nel caso di cancellazione della richiesta, il senderId è l'utente che cancella la richiesta, altrimenti se una richiesta è rigettata è il contrario (gestito lato client)
    @POST("api/auth/deleteFriendRequest")
    suspend fun deleteFriendRequest(@Body request: FriendRequestField): GenericFriendResponse
}