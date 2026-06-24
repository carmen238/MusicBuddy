package com.example.musicbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.musicbuddy.data.models.getChatId
import com.example.musicbuddy.repository.ChatRepository
import com.example.musicbuddy.ui.auth.AuthViewModel
import com.example.musicbuddy.ui.auth.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    friendId: String,
    currentUserId: String,
    friendName: String,
    friendSurname: String,
    onBackClick: () -> Unit = {},
    viewModel: ChatViewModel
) {
    val messages by viewModel.messages.collectAsState()
    var text by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val chatId = getChatId(currentUserId, friendId)

    // 🔥 FIX 1: init solo una volta
    val initialized = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!initialized.value) {
            viewModel.init(currentUserId)
            initialized.value = true
        }
    }

    // 🔥 FIX 2: load chat
    LaunchedEffect(chatId) {
        viewModel.loadMessages(chatId)
    }

    // scroll
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier.weight(1f),
            state = listState
        ) {
            items(
                messages,
                key = { it.id } // 🔥 FIX IMPORTANTE
            ) { message ->

                val isMine = message.senderId == currentUserId

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        if (isMine) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        color = if (isMine) Color(0xFF4CAF50) else Color.LightGray,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = message.text,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier.padding(8.dp)) {

            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Scrivi...") }
            )

            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        viewModel.sendMessage(
                            text,
                            currentUserId,
                            friendId
                        )
                        text = ""
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
            }
        }
    }
}