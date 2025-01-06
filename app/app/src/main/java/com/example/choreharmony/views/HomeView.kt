package com.example.choreharmony.views

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.choreharmony.viewmodel.HomeViewModel
import com.example.choreharmony.views.assets.CenterBoxText
import com.example.choreharmony.views.assets.TabulatedRow

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeView(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.getMyHousehold()
        viewModel.getHouseholdChores()
        viewModel.getMyChores()
    }

    if (viewModel.getHouseholdLoading.value ||
        viewModel.getHouseholdChoresLoading.value ||
        viewModel.getMyChoresLoading.value) {
        return Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }

    if (viewModel.household.value == null) {
        return Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = {
                        navController.navigate("create-household")
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .height(40.dp)
                ) {
                    Text("Create Household")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        navController.navigate("join-household")
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .height(40.dp)
                ) {
                    Text("Join Household")
                }
                Spacer(modifier = Modifier.height(4.dp))
                ClickableText(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = AnnotatedString("Manage pending requests"),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 16.sp
                    ),
                    overflow = TextOverflow.Ellipsis,
                    onClick = {
                        navController.navigate("manage-my-pending-requests")
                    }
                )
            }
        }
    }

    val clipboardManager = LocalClipboardManager.current

    return Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = viewModel.household.value!!.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
                actions = {
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(viewModel.household.value!!.join_code.uppercase()))
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy Join Code",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        Icon(
                            imageVector = Icons.Filled.Inbox,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column {
                    var selectedTabIndex by remember { mutableIntStateOf(0) }

                    TabulatedRow(
                        tabTitles = listOf("All Chores", "My Chores"),
                        selectedTabIndex = selectedTabIndex,
                        onTabSelected = { index ->
                            selectedTabIndex = index
                        })

                    if (viewModel.householdChores.value.isEmpty() && selectedTabIndex == 0) {
                        CenterBoxText(padding = padding, text = "This household has no chores, hooray!")
                    } else if (viewModel.myChores.value.isEmpty() && selectedTabIndex == 1) {
                        CenterBoxText(padding = padding, text = "You have no chores, hooray!")
                    }

                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)) {
                        items(if (selectedTabIndex == 0) viewModel.householdChores.value else viewModel.myChores.value) { chore ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        navController.navigate(
                                            "chore-details/{cId}".replace(
                                                oldValue = "{cId}",
                                                newValue = chore.id.toString()
                                            )
                                        )
                                    }
                            ) {
                                val isCompletionDropDownExpanded = remember { mutableStateOf(false)}
                                Column(Modifier.padding(8.dp)) {
                                    Row {
                                        Box(modifier =  Modifier.fillMaxSize()) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = chore.name,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(text = if (chore.assigned_user != null) "${chore.assigned_user!!.first_name} ${chore.assigned_user!!.last_name} " else "Unassigned")
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }

                if (viewModel.household.value!!.owner_id == viewModel.userRepository.currentUser.value!!.id.value) {
                    FloatingActionButton(
                        modifier= Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        onClick = { navController.navigate("create-chore") }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Create Chore", tint = Color.White)
                    }
                }
            }
        }
    )
}