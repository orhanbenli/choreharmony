package com.example.choreharmony.views


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.choreharmony.viewmodel.ChangePasswordViewModel

@Composable
fun ChangePasswordView(
    viewModel: ChangePasswordViewModel = hiltViewModel(),
    navController: NavController
) {
    Scaffold(
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
                        text = "Change Password",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.primary
            )
        },
        content = {
            var viewOldPassword by remember { mutableStateOf(false) }
            var viewNewPassword by remember { mutableStateOf(false) }
            var viewNewConfirmPassword by remember { mutableStateOf(false) }

            Column(
                Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    value = viewModel.oldPassword.value,
                    onValueChange = { value -> viewModel.oldPassword.value = value },
                    label = { Text("Old Password") },
                    visualTransformation = if (viewOldPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (viewOldPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (viewOldPassword) "Hide password" else "Show password"
                        IconButton(onClick = { viewOldPassword = !viewOldPassword }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    value = viewModel.newPassword.value,
                    onValueChange = { value -> viewModel.newPassword.value = value },
                    label = { Text("New Password") },
                    visualTransformation = if (viewNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (viewNewPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (viewNewPassword) "Hide password" else "Show password"
                        IconButton(onClick = { viewNewPassword = !viewNewPassword }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    value = viewModel.confirmNewPassword.value,
                    onValueChange = { value -> viewModel.confirmNewPassword.value = value },
                    label = { Text("Confirm New Password") },
                    visualTransformation = if (viewNewConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (viewNewConfirmPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (viewNewConfirmPassword) "Hide password" else "Show password"
                        IconButton(onClick = { viewNewConfirmPassword = !viewNewConfirmPassword }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                )
                if (viewModel.passwordSuccess.value != null) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = viewModel.passwordSuccess.value!!
                    )
                } else if (viewModel.passwordError.value != null) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.error,
                        text = viewModel.passwordError.value!!,
                        textAlign = TextAlign.Center
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                    {
                        TextButton(
                            onClick = {
                                viewModel.editPassword(
                                    viewModel.oldPassword.value,
                                    viewModel.newPassword.value
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text("Change Password")
                        }
                    }
                }
            }
        }
    )

}
