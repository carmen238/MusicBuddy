package com.example.musicbuddy.repository

import com.example.musicbuddy.data.models.ChatResponse
import com.example.musicbuddy.data.models.Message
import com.example.musicbuddy.network.ChatApi

class ChatRepository(
    private val api: ChatApi
) {

    suspend fun getMessages(chatId: String): ChatResponse {
        return api.getMessages(chatId)
    }

    suspend fun sendMessage(message: Message) {
        api.sendMessage(message)
    }
}