package com.example.choreharmony.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choreharmony.BuildConfig
import com.example.choreharmony.model.HouseholdMembership
import com.example.choreharmony.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class ManageSentJoinRequestsViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {
    var sentJoinRequests = MutableStateFlow<List<HouseholdMembership>>(emptyList())
    var loading = mutableStateOf(true)

    fun cancelRequest(requestId: Int, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "id" to requestId,
                    "approve" to false
                )
                val requestBodyString = gson.toJson(requestBody)

                val url = URL(BuildConfig.MANAGE_HOUSEHOLD_JOIN_REQUEST_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }

                    val responseCode = responseCode
                    println("Response:: $responseCode")
                    loading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun getSentHouseholdJoinRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            try {
                val obj = URL(BuildConfig.GET_SENT_HOUSEHOLD_JOIN_REQUESTS)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")
                val responseCode = con.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val sentRequestsList: List<HouseholdMembership> =
                        Gson().fromJson(response, object : TypeToken<List<HouseholdMembership>>() {}.type)
                    sentJoinRequests.value = sentRequestsList
                }

                loading.value = false

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}