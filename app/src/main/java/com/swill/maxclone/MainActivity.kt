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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    val messages = remember { mutableStateListOf<String>("Привет! Это MaxClone.") }
                    var input by remember { mutableStateOf("") }

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
                                    messages.add("Я: $input")
                                    input = ""
                                }
                            }) { Text("Send") }
                        }
                    }
                }
            }
        }
    }
}
