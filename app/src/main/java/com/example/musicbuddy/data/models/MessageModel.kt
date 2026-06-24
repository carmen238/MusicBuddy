package com.example.musicbuddy.data.models

/**
 * Message model
 */
data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val text: String,
    val timestamp: Long
)