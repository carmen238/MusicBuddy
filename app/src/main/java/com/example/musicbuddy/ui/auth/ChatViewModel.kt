package com.example.musicbuddy.ui.auth

import com.example.musicbuddy.network.ChatWebSocketManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicbuddy.data.models.Message
import com.example.musicbuddy.data.models.getChatId
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

    private var myUserId: String = ""
    private var currentChatId: String = ""

    private var initialized = false

    fun init(userId: String) {
        if (initialized) return
        initialized = true

        myUserId = userId
        socket = ChatWebSocketManager(userId)

        socket.onMessageReceived = { text, from, chatId ->

            if (chatId != currentChatId || from == myUserId) {
                // skip
            }
            else {

                val message = Message(
                    id = System.currentTimeMillis().toString(),
                    chatId = chatId,
                    senderId = from,
                    text = text,
                    timestamp = System.currentTimeMillis()
                )

                _messages.value = _messages.value + message
            }
        }

        socket.connect()
    }

    fun loadMessages(chatId: String) {
        currentChatId = chatId
        viewModelScope.launch {
            try {
                val response = repository.getMessages(chatId)
                _messages.value = response.messages
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

            val chatId = getChatId(currentUserId, friendId)

            val message = Message(
                id = System.currentTimeMillis().toString(),
                chatId = chatId,
                senderId = currentUserId,
                text = text,
                timestamp = System.currentTimeMillis()
            )

            try {
                // 🔥 REALTIME
                socket.sendMessage(
                    text = text,
                    chatId = chatId,
                    from = currentUserId
                )

                // 💾 SAVE DB
                repository.sendMessage(message)

                // 📱 UI (optimistic)
                _messages.value = _messages.value + message

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}