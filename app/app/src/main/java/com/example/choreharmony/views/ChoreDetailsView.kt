package com.example.choreharmony.views

//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.choreharmony.viewmodel.ChoreDetailsViewModel
import com.example.choreharmony.views.assets.CenterBoxText
import com.example.choreharmony.views.assets.ChoreCalendar
import com.example.choreharmony.views.assets.DetailCard

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ChoreDetailsView(
    viewModel: ChoreDetailsViewModel = hiltViewModel(),
    navController: NavController,
    cId : Int
) {
    LaunchedEffect(Unit) {
        viewModel.getDetailedChore(cId)
    }

    var showCalendarEvent by remember { mutableStateOf(false) }

    if (viewModel.getChoreByCIdLoading.value ||
        viewModel.completeChoreLoading.value ||
        viewModel.knockChoreLoading.value) {
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

     Scaffold (
        topBar = {
            TopAppBar (
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
                        text = if (viewModel.detailedChore.value != null) viewModel.detailedChore.value!!.name else "Chore Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
            )
        },
        content = { padding->
            if (viewModel.detailedChore.value == null) {
                return@Scaffold CenterBoxText(padding = padding, text = "Could not get chore details. Try again later.")
            }

            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {
                Column (modifier = Modifier.padding(16.dp)) {
                    if (viewModel.detailedChore.value!!.assigned_user == null) {
                        DetailCard(text = "Unassigned",
                            icon = Icons.Filled.Person,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp))
                    } else {
                        DetailCard(text = "${viewModel.detailedChore.value!!.assigned_user!!.first_name} ${viewModel.detailedChore.value!!.assigned_user!!.last_name}",
                            icon =Icons.Filled.Person,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp))
                    }

                    DetailCard(text = "Completed: " + viewModel.formatUTCDateString(viewModel.detailedChore.value!!.completion_date, "Never"),
                        icon = Icons.Filled.Checklist,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp))

                    if (viewModel.detailedChore.value!!.recurrence_in_days == null) {
                        DetailCard(text = "No Recurrence",
                            icon = Icons.Filled.CalendarMonth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp))
                    } else {
                        DetailCard(text = "Repeat every ${viewModel.detailedChore.value!!.recurrence_in_days} day${if (viewModel.detailedChore.value!!.recurrence_in_days!! > 1) "s" else ""}",
                            icon = Icons.Filled.CalendarMonth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp))
                    }

                    DetailCard(text = "Last Reminder: " + viewModel.formatUTCDateString(viewModel.detailedChore.value!!.last_reminder_date, "None"),
                        icon = Icons.Filled.DoorBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column (
                    modifier=Modifier.align(Alignment.BottomCenter)
                )
                {
                    if (viewModel.detailedChore.value!!.household.owner_id == viewModel.userRepository.currentUser.value!!.id.value) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                navController.navigate("reassign-chore/${viewModel.detailedChore.value!!.id}")
                            }) {
                            Text("Reassign Chore")
                        }
                    }

                    if (viewModel.detailedChore.value!!.assigned_user_id == viewModel.userRepository.currentUser.value!!.id.value) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                navController.navigate("trade-chore/${viewModel.detailedChore.value!!.id}")
                            })
                        {
                            Text("Trade Chore")
                        }
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showCalendarEvent = true
                        })
                    {
                        Text("Add To Calendar")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = viewModel.isCompleteEnabled(),
                        onClick = {
                            viewModel.completeChore(viewModel.detailedChore.value!!.id)
                        })
                    {
                        Text("Mark as complete")
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = viewModel.isKnockEnabled(),
                        onClick = {
                            viewModel.knock(viewModel.detailedChore.value!!.id)
                        })
                    {
                        Text("Knock")
                    }
                }
            }
        }
    )

    if (showCalendarEvent) {
        viewModel.detailedChore.value?.let { it1 ->
            ChoreCalendar(chore = it1)
            showCalendarEvent = false
        }
    }
}



