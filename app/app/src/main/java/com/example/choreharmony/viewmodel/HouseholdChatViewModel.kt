package com.example.choreharmony.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choreharmony.BuildConfig
import com.example.choreharmony.model.Household
import com.example.choreharmony.model.HouseholdChat
import com.example.choreharmony.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class HouseholdChatViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {
    var household = mutableStateOf<Household?>(null)
    var householdChats = MutableStateFlow<List<HouseholdChat>>(emptyList())

    var getHouseholdLoading = mutableStateOf(false)
    var getHouseholdChatsLoading = mutableStateOf(false)
    var deleteHouseholdChatLoading = mutableStateOf(false)
    var sendHouseholdChatLoading = mutableStateOf(false)

    var message = mutableStateOf("")

    fun isUserHouseholdOwner(): Boolean {
        return household.value!!.owner_id == userRepository.currentUser.value!!.id.value
    }

    fun isMessageSentByUser(post: HouseholdChat): Boolean {
        return post.user_id == userRepository.currentUser.value!!.id.value
    }

    fun getMyHousehold() {
        viewModelScope.launch(Dispatchers.IO) {
            getHouseholdLoading.value = true
            try {
                val url = URL(BuildConfig.GET_MY_HOUSEHOLD_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        household.value = Gson().fromJson(response, object : TypeToken<Household>() {}.type)
                    } else {
                        household.value = null
                    }
                    getHouseholdLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getHouseholdChats(withLoadingFlag: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            getHouseholdChatsLoading.value = withLoadingFlag && true
            try {
                val obj = URL(BuildConfig.HOUSEHOLD_CHATS_URL)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")
                val responseCode = con.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val householdChatList: List<HouseholdChat> =
                        Gson().fromJson(response, object : TypeToken<List<HouseholdChat>>() {}.type)
                    householdChats.value = householdChatList
                    startPolling()
                }

                getHouseholdChatsLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendHouseholdChat() {
        if (message.value.trim().isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            sendHouseholdChatLoading.value = true
            getHouseholdChatsLoading.value = true
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "text" to message.value
                )
                val requestBodyString = gson.toJson(requestBody)

                message.value = ""

                val url = URL(BuildConfig.HOUSEHOLD_CHATS_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }

                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        val householdChatList: List<HouseholdChat> =
                            Gson().fromJson(response, object : TypeToken<List<HouseholdChat>>() {}.type)
                        householdChats.value = householdChatList
                    }

                    sendHouseholdChatLoading.value = false
                    getHouseholdChatsLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                delay(60000)
                getHouseholdChats()
            }
        }
    }

    fun deleteHouseholdChat(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteHouseholdChatLoading.value = true
            getHouseholdChatsLoading.value = true
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "household_chat_id" to id
                )
                val requestBodyString = gson.toJson(requestBody)

                message.value = ""

                val url = URL(BuildConfig.HOUSEHOLD_CHATS_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "DELETE"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        val householdChatList: List<HouseholdChat> =
                            Gson().fromJson(response, object : TypeToken<List<HouseholdChat>>() {}.type)
                        householdChats.value = householdChatList
                    }

                    deleteHouseholdChatLoading.value = false
                    getHouseholdChatsLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}