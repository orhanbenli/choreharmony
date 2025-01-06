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
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class CreateHouseholdViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {
    var name = mutableStateOf("")
    var loading = mutableStateOf(false)
    var error = mutableStateOf("")

    fun createHousehold(callback: (Boolean) -> Unit) {
        if (name.value.isBlank()) {
            error.value = "Household name is required."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "name" to name.value
                )
                val requestBodyString = gson.toJson(requestBody)

                val url = URL(BuildConfig.CREATE_HOUSEHOLD_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }

                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        error.value = ""
                    } else {
                        error.value = "Could not create household."
                    }

                    loading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback(error.value == "")
            }
        }
    }
}