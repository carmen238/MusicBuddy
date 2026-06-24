package com.example.musicbuddy.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class ChatWebSocketManager(
    private val userId: String
) {

    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket

    var onMessageReceived: ((String, String) -> Unit)? = null

    fun connect() {
        val request = Request.Builder()
            .url("ws://10.0.2.2:3000")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {

                val register = """
                    {
                        "type": "REGISTER",
                        "userId": "$userId"
                    }
                """.trimIndent()

                webSocket.send(register)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val json = JSONObject(text)

                if (json.getString("type") == "MESSAGE") {
                    val message = json.getString("text")
                    val from = json.getString("from")

                    onMessageReceived?.invoke(message, from)
                }
            }
        })
    }

    fun sendMessage(text: String, to: String) {
        val message = """
            {
                "type": "MESSAGE",
                "text": "$text",
                "from": "$userId",
                "to": "$to"
            }
        """.trimIndent()

        webSocket.send(message)
    }

    fun disconnect() {
        webSocket.close(1000, null)
    }
}