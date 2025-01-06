package com.example.choreharmony.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.choreharmony.viewmodel.TradeRequestsViewModel
import com.example.choreharmony.views.assets.CenterBoxText
import com.example.choreharmony.views.assets.TabulatedRow

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TradeRequestsView(
    viewModel: TradeRequestsViewModel = hiltViewModel(),
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.getPendingTradeRequests()
        viewModel.getSentTradeRequests()
    }

    return Scaffold (
        topBar = {
            TopAppBar(
                navigationIcon =
                {
                    IconButton(
                        onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Text(
                        text = "Trade Requests",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.primary
            )
        },
        content = { padding ->
            if (viewModel.getPendingTradeRequestsLoading.value ||
                viewModel.getSentTradeRequestsLoading.value ||
                viewModel.deleteSentRequestLoading.value ||
                viewModel.managePendingRequestsLoading.value) {
                return@Scaffold Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }

            var selectedTabIndex by remember { mutableIntStateOf(0) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column {
                    TabulatedRow(tabTitles = listOf("Sent", "Pending"), selectedTabIndex = selectedTabIndex, onTabSelected = { index ->
                        selectedTabIndex = index
                    })

                    if (viewModel.pendingTradeRequests.value.isEmpty() && selectedTabIndex == 1) {
                        return@Scaffold CenterBoxText(padding = padding, text = "No pending requests")
                    }

                    if (viewModel.sentTradeRequests.value.isEmpty() && selectedTabIndex == 0) {
                        return@Scaffold CenterBoxText(padding = padding, text = "No sent requests")
                    }

                    LazyColumn(Modifier.fillMaxSize()) {
                        if (selectedTabIndex == 0) {
                            items(viewModel.sentTradeRequests.value) { tradeRequest ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column(Modifier.padding(8.dp)) {
                                        Row {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = tradeRequest.chore.name,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(text = "${tradeRequest.source_user.first_name} ${tradeRequest.source_user.last_name}  (-${tradeRequest.household_power})")
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                IconButton(onClick = {
                                                    viewModel.deleteSentTradeRequest(tradeRequest.id)
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Close,
                                                        contentDescription = "Cancel Request"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            items(viewModel.pendingTradeRequests.value) { tradeRequest ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column(Modifier.padding(8.dp)) {
                                        Row {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = tradeRequest.chore.name,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(text = "${tradeRequest.source_user.first_name} ${tradeRequest.source_user.last_name} (+${tradeRequest.household_power})")
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                IconButton(onClick = {
                                                    viewModel.managePendingTradeRequest(tradeRequest.id, false)
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Close,
                                                        contentDescription = "Deny Request"
                                                    )
                                                }
                                                IconButton(onClick = {
                                                    viewModel.managePendingTradeRequest(tradeRequest.id, true)
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Check,
                                                        contentDescription = "Accept Request"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}