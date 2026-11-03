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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()
            
        val serverUrl = "wss://mysty-dawn-51cd.serafimabramovbirukov.workers.dev/"
        val request = Request.Builder().url(serverUrl).build()
        
        var webSocket: WebSocket? = null

        setContent {
            MaterialTheme {
                Surface {
                    val messages = remember { mutableStateListOf<String>() }
                    var input by remember { mutableStateOf("") }
                    var isConnected by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        webSocket = client.newWebSocket(request, object : WebSocketListener() {
                            override fun onOpen(webSocket: WebSocket, response: Response) {
                                runOnUiThread {
                                    isConnected = true
                                    messages.add("Система: Подключено!")
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
                            ) { Text("->") } // Заменили стрелку на обычный текст
                        }
                    }
                }
            }
        }
    }
}
