package com.example.musicbuddy.data.models

/**
 * Message model
 */
data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val text: String,
    val timestamp: Long
)

fun getChatId(userA: String, userB: String): String {
    return if (userA < userB) {
        "$userA-$userB"
    } else {
        "$userB-$userA"
    }
}

data class ChatResponse(
    val success: Boolean,
    val messages: List<Message>
)