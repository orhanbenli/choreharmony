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
class CreateChoreViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {
    var name = mutableStateOf("")
    var assignedMember = mutableStateOf<ExternalUser?>(null)
    var recurrenceInDays = mutableStateOf<Int?>(null)

    var assignableMembers = MutableStateFlow<List<ExternalUser>>(emptyList())
    var allValuesError = mutableStateOf<String?>(null)

    var createChoreLoading = mutableStateOf(false)
    var getAssignableMembersLoading = mutableStateOf(false)

    fun getAssignableMembers() {
        viewModelScope.launch(Dispatchers.IO) {
            getAssignableMembersLoading.value = true
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
                getAssignableMembersLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createChore(callback: () -> Unit) {
        if (name.value.trim().isEmpty()) {
            allValuesError.value = "The name of the chore is required"
            return
        }

        if (recurrenceInDays.value != null && recurrenceInDays.value!! <= 0) {
            allValuesError.value = "Invalid recurrence value"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            createChoreLoading.value = true
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "assigned_user_id" to if (assignedMember.value != null) assignedMember.value!!.id else null,
                    "name" to name.value.trim(),
                    "recurrence_in_days" to if (recurrenceInDays.value != null) recurrenceInDays.value!! else null
                )
                val requestBodyString = gson.toJson(requestBody)

                val url = URL(BuildConfig.CREATE_CHORE_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }

                    val responseCode = responseCode
                    println("Response:: $responseCode")

                    createChoreLoading.value = false

                    if (responseCode != HttpURLConnection.HTTP_CREATED) {
                        allValuesError.value = "Could not create chore"
                        return@launch
                    }
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