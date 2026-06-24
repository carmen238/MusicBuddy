package com.example.musicbuddy.repository

import com.example.musicbuddy.data.models.Message
import com.example.musicbuddy.network.ChatApi

class ChatRepository(
    private val api: ChatApi
) {

    suspend fun getMessages(friendId: String): List<Message> {
        return api.getMessages(friendId)
    }

    suspend fun sendMessage(message: Message) {
        api.sendMessage(message)
    }
}