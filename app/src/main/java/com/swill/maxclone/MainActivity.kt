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
import io.socket.client.IO
import io.socket.client.Socket

class MainActivity : ComponentActivity() {
    private lateinit var socket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Твой сервер на Cloudflare
        val serverUrl = "wss://mysty-dawn-51cd.serafimabramovbirukov.workers.dev/"
        
        try {
            socket = IO.socket(serverUrl)
            socket.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            MaterialTheme {
                Surface {
                    val messages = remember { mutableStateListOf<String>("Подключение к серверу...") }
                    var input by remember { mutableStateOf("") }

                    LaunchedEffect(Unit) {
                        socket.on("message") { args ->
                            val msg = args[0].toString()
                            runOnUiThread {
                                if (messages.size == 1 && messages[0] == "Подключение к серверу...") {
                                    messages.clear()
                                }
                                messages.add(msg)
                            }
                        }
                    }

                    Column(Modifier.fillMaxSize().padding(16.dp)) {
                        LazyColumn(Modifier.weight(1f)) {
                            items(messages) { m -> Text(m, Modifier.padding(4.dp)) }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = input,
                                onValueChange = { input = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Сообщение") }
                            )
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = {
                                if (input.isNotBlank()) {
                                    socket.emit("message", input)
                                    input = ""
                                }
                            }) { Text("→") }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::socket.isInitialized) {
            socket.disconnect()
        }
    }
}
