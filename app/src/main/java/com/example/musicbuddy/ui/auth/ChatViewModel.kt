package com.example.musicbuddy.ui.auth

import ChatWebSocketManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicbuddy.data.models.Message
import com.example.musicbuddy.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private lateinit var socket: ChatWebSocketManager
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    fun init(userId: String) {
        socket = ChatWebSocketManager(userId)

        socket.onMessageReceived = { text, from ->

            val message = Message(
                id = System.currentTimeMillis().toString(),
                senderId = from,
                receiverId = userId,
                text = text,
                timestamp = System.currentTimeMillis()
            )

            _messages.value = _messages.value + message
        }

        socket.connect()
    }

    fun loadMessages(friendId: String) {
        viewModelScope.launch {
            try {
                _messages.value = repository.getMessages(friendId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(
        text: String,
        currentUserId: String,
        friendId: String
    ) {
        viewModelScope.launch {

            val message = Message(
                id = System.currentTimeMillis().toString(),
                senderId = currentUserId,
                receiverId = friendId,
                text = text,
                timestamp = System.currentTimeMillis()
            )

            try {

                // 🔥 REALTIME (WEBSOCKET)
                socket.sendMessage(text, friendId)

                // 💾 SALVATAGGIO (HTTP)
                repository.sendMessage(message)

                // UI ottimistica
                _messages.value = _messages.value + message

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}