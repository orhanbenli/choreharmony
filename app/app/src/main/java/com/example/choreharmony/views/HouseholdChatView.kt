package com.example.choreharmony.views


import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.choreharmony.viewmodel.HouseholdChatViewModel
import com.example.choreharmony.views.assets.CenterBoxText
import com.example.choreharmony.views.assets.MessageBubble
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition", "CoroutineCreationDuringComposition")
@Composable
fun HouseholdChatView(
    viewModel: HouseholdChatViewModel = hiltViewModel(),
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.getMyHousehold()
        viewModel.getHouseholdChats(true)
    }

    return Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Household Chat",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.primary
            )
        },
        content = { padding ->
            if (viewModel.getHouseholdLoading.value ||
                viewModel.getHouseholdChatsLoading.value ||
                viewModel.sendHouseholdChatLoading.value
            ) {
                return@Scaffold Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }

            if (viewModel.household.value == null) {
                return@Scaffold CenterBoxText(padding = padding, text = "Join or create a household to access the chat.")
            }

            val state = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(enabled = false, state = rememberScrollState())
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.Bottom,
                    userScrollEnabled = true,
                    state = state
                ) {
                    itemsIndexed(viewModel.householdChats.value) { index, post ->
                        val name =
                            if (viewModel.isMessageSentByUser(post))
                                "You"
                            else post.user.first_name

                        val formattedDateTime = LocalDateTime
                            .parse(post.create_date, DateTimeFormatter.ISO_DATE_TIME)
                            .atOffset(ZoneOffset.UTC)
                            .atZoneSameInstant(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm", Locale.ENGLISH))
                        MessageBubble(
                            isOwner = viewModel.isUserHouseholdOwner(),
                            isMe = viewModel.isMessageSentByUser(post),
                            name = "$name (${formattedDateTime})",
                            message = post.message,
                            onDelete = {
                                viewModel.deleteHouseholdChat(post.id)
                            }
                        )
                        coroutineScope.launch {
                            state.animateScrollToItem(viewModel.householdChats.value.size - 1)
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (viewModel.getHouseholdLoading.value ||
                viewModel.getHouseholdChatsLoading.value ||
                viewModel.household.value == null) {
                return@Scaffold
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    singleLine = true,
                    value = viewModel.message.value,
                    onValueChange = { viewModel.message.value = it },
                    label = { Text("Message") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(5.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                if (viewModel.sendHouseholdChatLoading.value ||
                    viewModel.deleteHouseholdChatLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(5.dp)
                            .align(Alignment.CenterVertically),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                } else {
                    IconButton(
                        onClick = {
                            if (viewModel.message.value.trim().isNotEmpty()) {
                                viewModel.sendHouseholdChat()
                            }
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Send chat",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    )
}