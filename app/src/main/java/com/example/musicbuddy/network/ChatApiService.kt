package com.example.musicbuddy.network

import com.example.musicbuddy.data.models.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ChatApi {

    @GET("api/chat/messages/{chatId}")
    suspend fun getMessages(
        @Path("chatId") chatId: String
    ): ChatResponse

    @POST("api/chat/messages")
    suspend fun sendMessage(
        @Body message: Message
    )
}