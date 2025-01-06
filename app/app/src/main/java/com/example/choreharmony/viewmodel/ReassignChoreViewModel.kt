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
class ReassignChoreViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {
    var assignableMembersLoading = mutableStateOf(false)
    var reassignChoreLoading = mutableStateOf(false)
    var assignableMembers = MutableStateFlow<List<ExternalUser>>(emptyList())
    var allErrorsValue = mutableStateOf("")

    var assignedMember = mutableStateOf<ExternalUser?>(null)

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

    fun reassignChore(choreId: Int, callback: () -> Unit) {
        if (assignedMember.value == null) {
            allErrorsValue.value = "You must set a user to assign the chore to."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            reassignChoreLoading.value = true
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "chore_id" to choreId,
                    "user_id" to assignedMember.value!!.id
                )
                val requestBodyString = gson.toJson(requestBody)

                val url = URL(BuildConfig.REASSIGN_CHORE_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }

                    val responseCode = responseCode
                    println(responseCode)
                    reassignChoreLoading.value = false
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