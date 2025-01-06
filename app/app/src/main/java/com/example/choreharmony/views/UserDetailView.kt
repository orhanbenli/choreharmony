package com.example.choreharmony.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Scoreboard
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.choreharmony.viewmodel.UserDetailViewModel
import com.example.choreharmony.views.assets.DetailCard
import java.text.NumberFormat

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun UserDetailView(
    viewModel: UserDetailViewModel = hiltViewModel(),
    navController: NavController,
    userId: Int,
    approved: Boolean
) {
    LaunchedEffect(Unit) {
        viewModel.getUserDetails(userId)
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
                        text = "User Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.primary
            )
        },
        content = { padding ->
            if (viewModel.getUserDetailsLoading.value || viewModel.userDetails.value == null) {
                return@Scaffold Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }

            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {
                Column(modifier=Modifier.padding(16.dp)) {
                    DetailCard(text = "${viewModel.userDetails.value!!.first_name} ${viewModel.userDetails.value!!.last_name}", icon = Icons.Filled.Person)

                    Spacer(modifier = Modifier.height(16.dp))

                    DetailCard(text = NumberFormat.getIntegerInstance().format(viewModel.userDetails.value!!.household_power), icon = Icons.Filled.Scoreboard)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Reviews", fontWeight = FontWeight.Bold)
                    HorizontalDivider()
                    LazyColumn {
                        items(viewModel.userDetails.value!!.reviews) { review ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Row {
                                    Column(Modifier.padding(8.dp)) {
                                        Row {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = review.reviewer.first_name,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(text = review.comment.comment)
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                Column {
                                                    Icon(
                                                        imageVector = if (review.like) Icons.Filled.ThumbUp else Icons.Filled.ThumbDown,
                                                        contentDescription = "Like"
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Column (
                        modifier=Modifier.align(Alignment.BottomCenter)
                    )
                    {
                        if (approved && viewModel.userRepository.currentUser.value!!.id.value != viewModel.userDetails.value!!.id) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navController.navigate("review-user/${viewModel.userDetails.value!!.id}")
                                })
                            {
                                Text("Review User")
                            }
                        }
                    }
                }
            }
        }
    )
}