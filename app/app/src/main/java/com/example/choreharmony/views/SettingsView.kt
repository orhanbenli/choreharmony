package com.example.choreharmony.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Switch
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.choreharmony.viewmodel.SettingsViewModel
import com.example.choreharmony.views.assets.SettingsDetailCard


@Composable
fun SettingsView(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    LaunchedEffect(Unit) {
        homeViewModel.getMyHousehold()
        settingsViewModel.getEmailNotifications()
    }

    var showDeleteHousehold by remember { mutableStateOf(false) }
    var showLeaveHousehold by remember { mutableStateOf(false) }
    var showDeleteUser by remember { mutableStateOf(false) }
    var showDownloadData by remember { mutableStateOf(false) }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${settingsViewModel.userRepository.currentUser.value!!.firstName.value} ${settingsViewModel.userRepository.currentUser.value!!.lastName.value}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                SettingsDetailCard(text = "Log Out", icon = Icons.Filled.Logout, onClick = {
                    settingsViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(navController.graph.id)
                    }
                })

                Spacer(modifier = Modifier.height(16.dp))

                SettingsDetailCard(text = "Trade Requests", icon = Icons.Filled.SwapVert, onClick = {
                    navController.navigate("trade-requests")
                })

                Spacer(modifier = Modifier.height(16.dp))

                SettingsDetailCard(
                    text = "Change Password",
                    icon = Icons.Filled.Password,
                    onClick = {
                        navController.navigate("change-password")
                    })

                Spacer(modifier = Modifier.height(16.dp))

                SettingsDetailCard(text = "Download Data", icon = Icons.Filled.Download, onClick = {
                    showDownloadData = true
                })

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Actions", fontWeight = FontWeight.Bold)
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                text = "Email Notifications",
                                fontWeight = FontWeight.Bold
                            )
                            Column(Modifier.fillMaxWidth()) {
                                Switch(
                                    modifier = Modifier.align(Alignment.End),
                                    checked = settingsViewModel.emailNotificationsEnabled.value,
                                    enabled = !settingsViewModel.emailLoading.value,
                                    onCheckedChange = { value ->
                                        settingsViewModel.emailNotifications(value) {
                                            settingsViewModel.emailNotificationsEnabled.value =
                                                value
                                        }
                                    })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Danger Zone", fontWeight = FontWeight.Bold)
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                if (homeViewModel.household.value != null) {
                    if (homeViewModel.household.value!!.owner_id == settingsViewModel.userRepository.currentUser.value!!.id.value) {
                        SettingsDetailCard(
                            text = "Delete Household",
                            icon = Icons.Filled.Delete,
                            onClick = {
                                showDeleteHousehold = true
                            })
                    } else {
                        SettingsDetailCard(
                            text = "Leave Household",
                            icon = Icons.Filled.DirectionsRun,
                            onClick = {
                                showLeaveHousehold = true
                            })
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                SettingsDetailCard(
                    text = "Delete Account",
                    icon = Icons.Filled.DeleteForever,
                    onClick = {
                        showDeleteUser = true
                    })
            }
        }
    }

    if (showDeleteHousehold) {
        AlertDialog(title = {
            Text(text = "Delete Your Household?")
        }, onDismissRequest = {
            showDeleteHousehold = false
        }, confirmButton = {
            TextButton(onClick = {
                settingsViewModel.deleteHousehold {
                    showDeleteHousehold = false
                    navController.navigate("home")
                }
            }) {
                Text("Confirm")
            }
        }, dismissButton = {
            TextButton(onClick = {
                showDeleteHousehold = false
            }) {
                Text("Close")
            }
        })
    }

    if (showLeaveHousehold) {
        AlertDialog(title = {
            Text(text = "Leave Your Household?")
        }, onDismissRequest = {
            showLeaveHousehold = false
        }, confirmButton = {
            TextButton(onClick = {
                settingsViewModel.leaveHousehold {
                    showLeaveHousehold = false
                    navController.navigate("home")
                }
            }) {
                Text("Confirm")
            }
        }, dismissButton = {
            TextButton(onClick = {
                showLeaveHousehold = false
            }) {
                Text("Close")
            }
        })
    }

    if (showDeleteUser) {
        AlertDialog(title = {
            Text(text = "Delete Your Account?")
        }, onDismissRequest = {
            showDeleteUser = false
        }, confirmButton = {
            TextButton(onClick = {
                settingsViewModel.deleteAccount {
                    showDeleteUser = false
                    settingsViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(navController.graph.id)
                    }
                }
            }) {
                Text("Confirm")
            }
        }, dismissButton = {
            TextButton(onClick = {
                showDeleteUser = false
            }) {
                Text("Close")
            }
        })
    }

    if (showDownloadData) {
        AlertDialog(title = {
            Text(text = "Download your data?")
        }, onDismissRequest = {
            showDownloadData = false
        }, confirmButton = {
            TextButton(onClick = {
                settingsViewModel.downloadData {
                    showDownloadData = false
                }
            }) {
                Text("Confirm")
            }
        }, dismissButton = {
            TextButton(onClick = {
                showDownloadData = false
            }) {
                Text("Close")
            }
        })
    }
}