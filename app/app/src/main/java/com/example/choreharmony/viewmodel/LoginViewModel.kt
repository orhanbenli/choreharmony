package com.example.choreharmony.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choreharmony.BuildConfig
import com.example.choreharmony.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {
    var email = mutableStateOf("")
    var password = mutableStateOf("")

    var allValuesError = mutableStateOf<String?>(null)
    val loginError = mutableStateOf<String?>(null)

    fun userIsLoggedIn(): Boolean {
        if (userRepository.currentUser.value == null) return false
        return userRepository.currentUser.value!!.token != null
    }

    fun areAllValuesFilled(email: String, password: String) {
        if (email.trim().isNotEmpty() && password.trim().isNotEmpty()) {
            allValuesError.value = null
        } else {
            allValuesError.value = "Please fill in all values."
        }
    }

    fun login(callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var loggedIn = false
            try {
                val url = URL(BuildConfig.LOGIN_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")

                    val body = """{"email": "${email.value}", "password": "${password.value}"}"""
                    OutputStreamWriter(outputStream).use { it.write(body) }

                    val responseCode = responseCode
                    loggedIn = responseCode == HttpURLConnection.HTTP_OK
                    if (loggedIn) {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        val jsonResponse = JSONObject(response)
                        val token = jsonResponse.optString("data", null.toString())
                        userRepository.setUserToken(token)
                        println("Response: $response")
                    } else {
                        loginError.value = "Invalid Email or Password"
                        val response = errorStream.bufferedReader().use { it.readText() }
                        println("Error Response: $response")
                    }
                }
            } catch (e: Exception) {
                loginError.value = "Invalid Email or Password"
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback(loggedIn)
            }
        }
    }
}