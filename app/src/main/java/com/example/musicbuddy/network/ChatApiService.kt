package com.example.musicbuddy.network

import com.example.musicbuddy.data.models.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ChatApi {

    @GET("chat/messages/{friendId}")
    suspend fun getMessages(
        @Path("friendId") friendId: String
    ): List<Message>

    @POST("chat/send")
    suspend fun sendMessage(
        @Body message: Message
    )
}