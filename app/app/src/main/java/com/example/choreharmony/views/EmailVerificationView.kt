package com.example.choreharmony.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.choreharmony.viewmodel.EmailVerificationViewModel
import com.example.choreharmony.views.assets.CodeInputField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailVerificationView(
    viewModel: EmailVerificationViewModel = hiltViewModel(),
    navController: NavController
) {
    val codeLength = 6
    val verificationCode = remember { mutableStateListOf(*Array(codeLength) { "" }) }
    val focusRequesters =
        List(codeLength) { FocusRequester() } // move focus to a specific composable
    val verificationError by viewModel.verificationError
    var isButtonEnabled by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(30) }
    val coroutineScope = rememberCoroutineScope()
    var countdownStarted by remember { mutableStateOf(false) }


    if (!countdownStarted) {
        coroutineScope.launch {
            countdownStarted = true
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            isButtonEnabled = true
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            colors = TopAppBarColors(
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                Color.Black
            ),
            title = { },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
        Text(
            text = "Verification",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter the verification code to complete sign up",
            textAlign = TextAlign.Center
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(16.dp)
        ) {
            for (i in 0 until codeLength) {
                CodeInputField(
                    verificationCode[i],
                    onValueChange = {
                        if (it.length <= 1) {
                            verificationCode[i] = it
                            if (it.length == 1 && i < codeLength - 1) {
                                focusRequesters[i + 1].requestFocus()
                            }
                        }
                    },
                    focusRequester = focusRequesters[i],
                    onBackspace = {
                        if (i > 0) {
                            focusRequesters[i - 1].requestFocus()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .focusRequester(focusRequesters[i])
                )
            }
        }
        Button(
            onClick = {
                // Launch a coroutine in the viewModelScope or lifecycleScope
                viewModel.viewModelScope.launch {
                    val verificationCodeString = verificationCode.joinToString(separator = "")
                    viewModel.code.value = verificationCodeString
                    viewModel.register { isRegistered ->
                        if (isRegistered) {
                            navController.navigate("home")
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Done")
        }

        Button(
            onClick = {
                viewModel.sendVerificationEmail()
                isButtonEnabled = false
                timeLeft = 30
                coroutineScope.launch {
                    while (timeLeft > 0) {
                        delay(1000)
                        timeLeft--
                    }
                    isButtonEnabled = true
                }


            },
            enabled = isButtonEnabled
        ) {
            Text("Resend code")
        }
        if (!isButtonEnabled) {
            // Display the countdown
            Text("Resend code in $timeLeft seconds")
        }
        if (verificationError != null) {
            Text(
                text = verificationError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}