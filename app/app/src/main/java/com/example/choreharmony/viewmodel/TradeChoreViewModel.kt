package com.example.choreharmony.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choreharmony.BuildConfig
import com.example.choreharmony.model.ExternalUser
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
class TradeChoreViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {
    var assignableMembersLoading = mutableStateOf(false)
    var createTradeLoading = mutableStateOf(false)
    var assignableMembers = MutableStateFlow<List<ExternalUser>>(emptyList())

    var assignedMember = mutableStateOf<ExternalUser?>(null)
    var householdPower = mutableStateOf<Int?>(null)
    var allErrorsValue = mutableStateOf("")

    fun validateHouseholdPower(): Boolean {
        if (householdPower.value == null) return false
        return !(householdPower.value!! <= 0 || householdPower.value!! > 100)
    }

    fun createTradeRequest(choreId: Int, callback: () -> Unit) {
        if (!validateHouseholdPower()) {
            allErrorsValue.value = "Household power to transfer must be within 0 and 100."
            return
        }

        if (assignedMember.value == null) {
            allErrorsValue.value = "You must assign a user to trade the chore with."
            return
        }

        if (assignedMember.value!!.id == userRepository.currentUser.value!!.id.value) {
            allErrorsValue.value = "You can't trade a chore with yourself."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            createTradeLoading.value = true
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "household_power" to householdPower.value,
                    "chore_id" to choreId,
                    "destination_user_id" to assignedMember.value!!.id
                )
                val requestBodyString = gson.toJson(requestBody)

                val url = URL(BuildConfig.CREATE_TRADE_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }

                    val responseCode = responseCode
                    println(responseCode)
                    createTradeLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun getAssignableMembers() {
        viewModelScope.launch(Dispatchers.IO) {
            assignableMembersLoading.value = true
            try {
                val obj = URL(BuildConfig.GET_CHORE_ASSIGNABLE_MEMBERS_URL)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")
                val responseCode = con.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val assignableUsers: List<ExternalUser> =
                        Gson().fromJson(response, object : TypeToken<List<ExternalUser>>() {}.type)
                    assignableMembers.value = assignableUsers
                }
                assignableMembersLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}