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
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class JoinHouseholdViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {
    var joinCode = mutableStateOf("")
    var loading = mutableStateOf(false)
    var error = mutableStateOf("")
    var successMessage = mutableStateOf("")

    fun sendJoinRequest() {
        if (joinCode.value.trim().isBlank()) {
            error.value = "The household code is required."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "joinCode" to joinCode.value
                )
                val requestBodyString = gson.toJson(requestBody)

                val url = URL(BuildConfig.SEND_HOUSEHOLD_JOIN_REQUEST)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }

                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        error.value = ""
                        successMessage.value = "Sent a request to ${joinCode.value.uppercase()}!"
                    } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                        error.value = "There is no household with the provided code."
                    } else if (responseCode == HttpURLConnection.HTTP_CONFLICT) {
                        error.value = "You have already sent a request to this household."
                    } else {
                        error.value = "Could not send household join request."
                    }

                    loading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}