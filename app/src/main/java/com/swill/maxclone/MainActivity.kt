package com.swill.maxclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import okhttp3.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS) 
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Твой сервер на Cloudflare
        val serverUrl = "wss://mysty-dawn-51cd.serafimabramovbirukov.workers.dev/"
        
        setContent {
            MaterialTheme {
                Surface {
                    val messages = remember { mutableStateListOf<String>() }
                    var input by remember { mutableStateOf("") }
                    var isConnected by remember { mutableStateOf(false) }

                    // Функция подключения к серверу
                    fun connectToServer() {
                        val request = Request.Builder().url(serverUrl).build()
                        webSocket = client.newWebSocket(request, object : WebSocketListener() {
                            override fun onOpen(webSocket: WebSocket, response: Response) {
                                runOnUiThread {
                                    isConnected = true
                                    messages.add("Система: Подключено к серверу!")
                                }
                            }
                            override fun onMessage(webSocket: WebSocket, text: String) {
                                runOnUiThread {
                                    messages.add(text)
                                }
                            }
                            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                                runOnUiThread {
                                    isConnected = false
                                    messages.add("Система: Ошибка подключения")
                                }
                            }
                        })
                    }

                    // Запускаем подключение при старте экрана
                    LaunchedEffect(Unit) {
                        connectToServer()
                    }

                    Column(Modifier.fillMaxSize().padding(16.dp)) {
                        LazyColumn(Modifier.weight(1f)) {
                            items(messages) { m -> Text(m, Modifier.padding(4.dp)) }
                        }
                        
                        if (!isConnected) {
                            Text("Подключение к серверу...", modifier = Modifier.padding(bottom = 8.dp))
                        }

                        Row(Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = input,
                                onValueChange = { input = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Сообщение") }
                            )
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (input.isNotBlank() && isConnected) {
                                        webSocket?.send(input)
                                        input = ""
                                    }
                                },
                                enabled = isConnected
                            ) { Text("→") }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket?.close(1000, "Bye")
    }
}
