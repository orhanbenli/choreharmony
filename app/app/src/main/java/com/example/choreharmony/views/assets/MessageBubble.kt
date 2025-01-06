package com.example.choreharmony.views.assets

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MessageBubble(
    isOwner: Boolean,
    isMe: Boolean,
    name: String?,
    message: String,
    onDelete: () -> Unit
) {
    var showDelDialog by remember { mutableStateOf(false) }
    if (showDelDialog) {
        AlertDialog(
            onDismissRequest = { showDelDialog = false },
            title = { Text("Delete Message") },
            text = { Text("Are you sure you want to delete this message?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDelDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDelDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    Column(
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        name?.let { name ->
            Text(
                text = name,
                modifier = Modifier.background(
                    color = Color.Transparent
                ),
                fontSize = 12.sp,
            )
        }
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 48f,
                        topEnd = 48f,
                        bottomStart = if (isMe) 48f else 0f,
                        bottomEnd = if (isMe) 0f else 48f
                    )
                )
                .background(if (isMe) MaterialTheme.colorScheme.primary else Color.LightGray)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            if (isMe || isOwner) {
                                showDelDialog = true
                            }
                        }
                    )
                }
                .padding(16.dp)
        ) {
            Text(
                text = message,
                modifier = Modifier.background(
                    color = Color.Transparent
                ),
                color = if (isMe) Color.White else Color.Black,
                fontSize = 15.sp,
            )
        }
    }
}