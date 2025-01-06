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
class SettingsViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {
    var deleteHouseholdLoading = mutableStateOf(false)
    var leaveHouseholdLoading = mutableStateOf(false)
    var deleteAccountLoading = mutableStateOf(false)
    var downloadDataLoading = mutableStateOf(false)
    var emailLoading = mutableStateOf(false)
    var emailNotificationsEnabled = mutableStateOf(false)

    fun logout() {
        userRepository.resetUser()
    }

    fun deleteHousehold(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteHouseholdLoading.value = true
            try {
                val url = URL(BuildConfig.DELETE_HOUSEHOLD_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "DELETE"
                    doOutput = true
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    val responseCode = responseCode
                    println("Response :: $responseCode")

                    deleteHouseholdLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun leaveHousehold(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            leaveHouseholdLoading.value = true
            try {
                val url = URL(BuildConfig.LEAVE_HOUSEHOLD_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "DELETE"
                    doOutput = true
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    val responseCode = responseCode
                    println("Response :: $responseCode")
                    leaveHouseholdLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun deleteAccount(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteAccountLoading.value = true
            try {
                val url = URL(BuildConfig.DELETE_ACCOUNT_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "DELETE"
                    doOutput = true
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    val responseCode = responseCode
                    println("Response :: $responseCode")
                    deleteAccountLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun emailNotifications(enabled: Boolean, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            emailLoading.value = true
            try {
                val url = URL(BuildConfig.EMAIL_NOTIFICATIONS_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty(
                        "Authorization",
                        "Bearer " + userRepository.currentUser.value?.token.toString()
                    )

                    val jsonBody = """{ "enabled": $enabled }"""

                    OutputStreamWriter(outputStream).use { it.write(jsonBody) }

                    val responseCode = responseCode
                    println("Response :: $responseCode")
                    emailLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun getEmailNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            emailLoading.value = true
            try {
                val obj = URL(BuildConfig.EMAIL_NOTIFICATIONS_URL)
                (obj.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = inputStream.bufferedReader().use { it.readText() }

                        val jsonResponse = JSONObject(response)
                        emailNotificationsEnabled.value = jsonResponse.optBoolean("data", false)
                    }

                    emailLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun downloadData(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            downloadDataLoading.value = true
            try {
                val url = URL(BuildConfig.DOWNLOAD_DATA_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    val responseCode = responseCode
                    println("Response :: $responseCode")
                    downloadDataLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}