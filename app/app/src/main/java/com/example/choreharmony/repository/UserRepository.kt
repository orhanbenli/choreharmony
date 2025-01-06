package com.example.choreharmony.repository

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.auth0.jwt.JWT
import com.example.choreharmony.model.User

class UserRepository {
    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> get() = _currentUser

    fun createUser(
        firstName: MutableState<String>,
        lastName: MutableState<String>,
        email: MutableState<String>,
        password: MutableState<String>,
        id: MutableState<Int>
    ) {
        _currentUser.value = User(
            null,
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            id = id
        )
    }

    fun resetUser() {
        _currentUser.value = User(
            token = null,
            firstName = mutableStateOf(""),
            lastName = mutableStateOf(""),
            email = mutableStateOf(""),
            password = mutableStateOf(""),
            id = mutableIntStateOf(-1)
        )
    }

    fun setUserToken(newUserToken: String) {
        val decodedJWT = JWT.decode(newUserToken)
        decodedJWT?.let { jwt ->
            val firstName = jwt.getClaim("first_name").asString()
            val lastName = jwt.getClaim("last_name").asString()
            val id = jwt.getClaim("id").asInt()
            val email = jwt.getClaim("email").asString()
            val password = jwt.getClaim("password").asString()

            _currentUser.value = User(
                token = newUserToken,
                id = mutableIntStateOf(id),
                firstName = mutableStateOf(firstName),
                lastName = mutableStateOf(lastName),
                email = mutableStateOf(email),
                password = mutableStateOf(password)
            )
        }
    }
}