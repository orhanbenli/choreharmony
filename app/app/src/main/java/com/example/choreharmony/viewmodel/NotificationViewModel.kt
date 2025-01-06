package com.example.choreharmony.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choreharmony.BuildConfig
import com.example.choreharmony.model.Notification
import com.example.choreharmony.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {
    var notifications = MutableStateFlow<List<Notification>>(emptyList())
    var getNotificationsLoading = mutableStateOf(false)
    var deleteNotificationLoading = mutableStateOf(false)

    fun getNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            getNotificationsLoading.value = true
            try {
                val obj = URL(BuildConfig.GET_NOTIFICATIONS_URL)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")
                val responseCode = con.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val pendingMembers: List<Notification> =
                        Gson().fromJson(response, object : TypeToken<List<Notification>>() {}.type)
                    notifications.value = pendingMembers
                }

                getNotificationsLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteNotification(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteNotificationLoading.value = true
            try {
                val url = URL(BuildConfig.DELETE_NOTIFICATION_URL.replace(oldValue=":notificationId", newValue=id.toString()))
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "DELETE"
                    doOutput = true
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    val responseCode = responseCode

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        val pendingMembers: List<Notification> =
                            Gson().fromJson(response, object : TypeToken<List<Notification>>() {}.type)
                        notifications.value = pendingMembers
                    }

                    deleteNotificationLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}