package com.example.choreharmony.model

import androidx.compose.runtime.MutableState

data class User(
    var token: String?,
    val firstName: MutableState<String>,
    val lastName: MutableState<String>,
    val email: MutableState<String>,
    val password: MutableState<String>,
    val id: MutableState<Int>
)