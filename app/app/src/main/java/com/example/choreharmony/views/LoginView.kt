package com.example.choreharmony.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.choreharmony.viewmodel.LoginViewModel

@Composable
fun LoginView(
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavController
) {
    LaunchedEffect(Unit) {
        if (viewModel.userIsLoggedIn()) {
            navController.navigate("home")
            return@LaunchedEffect
        }
        viewModel.password.value = ""
        viewModel.email.value = ""
    }

    val allValuesError by viewModel.allValuesError
    val loginError by viewModel.loginError
    var viewPassword by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Login",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )
            OutlinedTextField(
                singleLine = true,
                value = viewModel.email.value,
                onValueChange = { viewModel.email.value = it },
                label = { Text("Email") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                singleLine = true,
                value = viewModel.password.value,
                onValueChange = { viewModel.password.value = it },
                label = { Text("Password") },
                visualTransformation = if (viewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image =
                        if (viewPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (viewPassword) "Hide password" else "Show password"
                    IconButton(onClick = { viewPassword = !viewPassword }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.areAllValuesFilled(
                        viewModel.email.value,
                        viewModel.password.value
                    )

                    viewModel.login { isLoggedIn ->
                        if (isLoggedIn) {
                            navController.navigate("home") {
                                popUpTo(navController.graph.id)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .width(300.dp)
                    .height(40.dp)
            ) {
                Text("Login")
            }
            if (allValuesError != null) {
                Text(
                    text = allValuesError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (allValuesError == null && loginError != null) {
                Text(
                    text = loginError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            ClickableText(
                text = AnnotatedString("Don't have an account? Sign up now!"),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 16.sp
                ),
                overflow = TextOverflow.Ellipsis,
                onClick = {
                    navController.navigate("signup")
                }
            )
        }
    }
}