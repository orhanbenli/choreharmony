package com.example.choreharmony.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choreharmony.BuildConfig
import com.example.choreharmony.model.Chore
import com.example.choreharmony.model.Household
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
class HomeViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {
    var household = mutableStateOf<Household?>(null)
    var pendingMemberRequests = MutableStateFlow<List<HouseholdMembership>>(emptyList())
    var householdChores = MutableStateFlow<List<Chore>>(emptyList())
    var myChores = MutableStateFlow<List<Chore>>(emptyList())

    var getHouseholdLoading = mutableStateOf(false)
    var getPendingJoinRequestsLoading = mutableStateOf(false)
    var manageJoinRequestsLoading = mutableStateOf(false)
    var getHouseholdChoresLoading = mutableStateOf(false)
    var getMyChoresLoading = mutableStateOf(false)

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

    fun getPendingJoinRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            getPendingJoinRequestsLoading.value = true
            try {
                val obj = URL(BuildConfig.GET_PENDING_HOUSEHOLD_JOIN_REQUESTS)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")
                val responseCode = con.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val pendingMembers: List<HouseholdMembership> =
                        Gson().fromJson(response, object : TypeToken<List<HouseholdMembership>>() {}.type)
                    pendingMemberRequests.value = pendingMembers
                }

                getPendingJoinRequestsLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun manageUserMembership(requestId: Int, approvalStatus: Boolean, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            manageJoinRequestsLoading.value = true
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "id" to requestId,
                    "approve" to approvalStatus
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
                    manageJoinRequestsLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun getHouseholdChores() {
        viewModelScope.launch(Dispatchers.IO) {
            getHouseholdChoresLoading.value = true
            try {
                val obj = URL(BuildConfig.GET_HOUSEHOLD_CHORES)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")
                val responseCode = con.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val allHouseholdChores: List<Chore> =
                        Gson().fromJson(response, object : TypeToken<List<Chore>>() {}.type)
                    householdChores.value = allHouseholdChores
                }

                getHouseholdChoresLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getMyChores() {
        viewModelScope.launch(Dispatchers.IO) {
            getMyChoresLoading.value = true
            try {
                val obj = URL(BuildConfig.GET_MY_CHORES)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")
                val responseCode = con.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val allUserChores: List<Chore> =
                        Gson().fromJson(response, object : TypeToken<List<Chore>>() {}.type)
                    myChores.value = allUserChores
                }

                getMyChoresLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}