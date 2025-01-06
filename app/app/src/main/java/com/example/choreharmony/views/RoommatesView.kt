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
import androidx.compose.material.Divider
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VerifiedUser
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
import com.example.choreharmony.viewmodel.HomeViewModel
import com.example.choreharmony.views.assets.CenterBoxText
import com.example.choreharmony.views.assets.TabulatedRow

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun RoommatesView(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.getMyHousehold()
        viewModel.getPendingJoinRequests()
    }

    return Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Household Roommates",
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
                viewModel.getPendingJoinRequestsLoading.value ||
                viewModel.manageJoinRequestsLoading.value) {
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

            if (viewModel.household.value == null) {
                return@Scaffold CenterBoxText(padding = padding, text = "Join or create a household to view your roommates.")
            }

            var selectedTabIndex by remember { mutableIntStateOf(0) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column {
                    TabulatedRow(tabTitles = listOf("Approved", "Pending Requests"), selectedTabIndex = selectedTabIndex, onTabSelected = { index ->
                        selectedTabIndex = index
                    })

                    if (viewModel.pendingMemberRequests.value.isEmpty() && selectedTabIndex == 1) {
                        return@Scaffold CenterBoxText(padding = padding, text = "No pending requests")
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        onClick = {
                            navController.navigate("user/${viewModel.household.value!!.owner_id}/approved")
                        }
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Row {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "${viewModel.household.value!!.owner.first_name} ${viewModel.household.value!!.owner.last_name}",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = viewModel.household.value!!.owner.email)
                                }
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = {

                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.VerifiedUser,
                                            contentDescription = "Remove Roommate"
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(horizontal = 16.dp))

                    if (viewModel.household.value!!.members.isEmpty() && selectedTabIndex == 0) {
                        return@Scaffold CenterBoxText(padding = padding, text = "No roommates")
                    }

                    LazyColumn(Modifier.fillMaxSize()) {
                        if (selectedTabIndex == 0) {
                            items(viewModel.household.value!!.members) { member ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    onClick = {
                                        navController.navigate("user/${member.user_id}/approved")
                                    }
                                ) {
                                    Column(Modifier.padding(8.dp)) {
                                        Row {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = "${member.user.first_name} ${member.user.last_name}",
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(text = member.user.email)
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                if (viewModel.household.value!!.owner_id == viewModel.userRepository.currentUser.value!!.id.value) {
                                                    IconButton(onClick = {
                                                        viewModel.manageUserMembership(member.id, false) {
                                                            viewModel.getMyHousehold()
                                                            viewModel.getPendingJoinRequests()
                                                        }
                                                    }) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Close,
                                                            contentDescription = "Remove Roommate"
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            items(viewModel.pendingMemberRequests.value) { member ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    onClick = {
                                        navController.navigate("user/${member.user_id}/pending")
                                    }
                                ) {
                                    Column(Modifier.padding(8.dp)) {
                                        Row {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = "${member.user.first_name} ${member.user.last_name}",
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(text = member.user.email)
                                            }
                                            if (viewModel.household.value!!.owner_id == viewModel.userRepository.currentUser.value!!.id.value) {
                                                Row(
                                                    modifier = Modifier
                                                        .padding(8.dp)
                                                        .fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.End
                                                ) {
                                                    IconButton(onClick = {
                                                        viewModel.manageUserMembership(member.id, false) {
                                                            viewModel.getMyHousehold()
                                                            viewModel.getPendingJoinRequests()
                                                        }
                                                    }) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Close,
                                                            contentDescription = "Deny Request"
                                                        )
                                                    }
                                                    IconButton(onClick = {
                                                        viewModel.manageUserMembership(member.id, true) {
                                                            viewModel.getMyHousehold()
                                                            viewModel.getPendingJoinRequests()
                                                        }
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
        }
    )
}