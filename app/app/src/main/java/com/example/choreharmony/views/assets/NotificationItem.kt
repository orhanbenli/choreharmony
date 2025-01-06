package com.example.choreharmony.views.assets

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.choreharmony.model.Notification
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.math.abs

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationItem(
    notification: Notification,
    navController: NavController
) {
    Card(
        onClick = {
            if (notification.notification_type == "CHAT") {
                navController.navigate("chat")
            } else if (notification.notification_type == "CHORE") {
                navController.navigate("chore-details/${notification.navigator_id}")
            } else if (notification.notification_type == "TRADE") {
                navController.navigate("trade-requests")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            val apiDateTime = OffsetDateTime.parse(notification.create_date)
            val duration = Duration.between(apiDateTime, OffsetDateTime.now())

            val days = duration.toDays()
            val hours = duration.toHours() % 24
            val minutes = duration.toMinutes() % 60
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = notification.content.take(
                                50
                            ),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when {
                                abs(days) > 0 -> "${abs(days)}d ago"
                                abs(hours) > 0 -> "${abs(hours)}h ago"
                                abs(minutes) >= 0 -> "${abs(minutes)}m ago"
                                else -> "just now"
                            }, fontWeight = FontWeight.Light,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
            }
        }
    }
}