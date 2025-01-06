package com.example.choreharmony.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choreharmony.BuildConfig
import com.example.choreharmony.repository.UserRepository
import com.google.gson.Gson
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
class EmailVerificationViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {
    private var firstName = userRepository.currentUser.value?.firstName
    private var lastName = userRepository.currentUser.value?.lastName
    private var email = userRepository.currentUser.value?.email
    private var password = userRepository.currentUser.value?.password
    var code = mutableStateOf("")
    var verificationError = mutableStateOf<String?>(null)

    fun getRegistrationRequest(): String {
        val gson = Gson()
        val requestBody = mapOf(
            "email" to email?.value,
            "password" to password?.value,
            "first_name" to firstName?.value,
            "last_name" to lastName?.value,
            "code" to code.value
        )

        return gson.toJson(requestBody)
    }

    fun register(callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var registered = false
            try {
                val url = URL(BuildConfig.REGISTER_API_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")

                    OutputStreamWriter(outputStream).use { it.write(getRegistrationRequest()) }

                    val responseCode = responseCode
                    registered = responseCode == HttpURLConnection.HTTP_CREATED
                    println(responseCode)
                    if (registered) {
                        // Handle the response
                        val response = inputStream.bufferedReader().use { it.readText() }
                        val jsonResponse = JSONObject(response)
                        val token = jsonResponse.optString("data", null.toString())

                        userRepository.setUserToken(token)
                        println("Registration Successful: $response")
                    } else {
                        // Handle error
                        verificationError.value = "The verification code is incorrect."
                        val response = errorStream.bufferedReader().use { it.readText() }
                        println("Registration Failed: $response")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback(registered)
            }
        }
    }

    fun sendVerificationEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL(BuildConfig.EMAIL_VERIFICATION_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")

                    val body = """{"email": "${email?.value}"}"""

                    OutputStreamWriter(outputStream).use { it.write(body) }
                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        val jsonResponse = JSONObject(response)
                        val token = jsonResponse.optString("data", null.toString())
                        userRepository.setUserToken(token)
                        println("Response: $response")
                    } else {
                        val response = errorStream.bufferedReader().use { it.readText() }
                        println("Error Response: $response")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}