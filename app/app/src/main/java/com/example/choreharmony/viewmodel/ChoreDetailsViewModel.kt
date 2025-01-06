package com.example.choreharmony.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choreharmony.BuildConfig
import com.example.choreharmony.model.Chore
import com.example.choreharmony.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChoreDetailsViewModel @Inject constructor(
    val userRepository: UserRepository,
): ViewModel(){
    var detailedChore = mutableStateOf<Chore?>(null)
    var getChoreByCIdLoading = mutableStateOf(false)
    var completeChoreLoading = mutableStateOf(false)
    var knockChoreLoading = mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.O)
    fun isKnockEnabled(): Boolean {
        if (detailedChore.value == null || userRepository.currentUser.value == null) return false
        if (detailedChore.value!!.assigned_user_id == null) return false

        if (detailedChore.value!!.last_reminder_date != null) {
            val lastReminderDateTime = LocalDateTime
                .parse(detailedChore.value!!.last_reminder_date, DateTimeFormatter.ISO_DATE_TIME)
                .atOffset(ZoneOffset.UTC)
                .toLocalDateTime()

            val now = LocalDateTime.now()

            if (now.isBefore(lastReminderDateTime.plusDays(1))) return false
        }

        return detailedChore.value!!.assigned_user_id != userRepository.currentUser.value!!.id.value
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isCompleteEnabled(): Boolean {
        if (detailedChore.value == null || userRepository.currentUser.value == null) return false
        if (detailedChore.value!!.completion_date == null) return true

        val lastCompletionDate = LocalDateTime
            .parse(detailedChore.value!!.completion_date, DateTimeFormatter.ISO_DATE_TIME)
            .atOffset(ZoneOffset.UTC)
            .toLocalDateTime()

        val now = LocalDateTime.now()

        return !now.isBefore(lastCompletionDate.plusDays(1))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatUTCDateString(date: String?, noValue: String): String {
        if (date == null) return noValue

        return LocalDateTime
            .parse(date, DateTimeFormatter.ISO_DATE_TIME)
            .atOffset(ZoneOffset.UTC)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm", Locale.ENGLISH))
    }

    fun getDetailedChore(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            getChoreByCIdLoading.value = true
            try {
                val obj = URL(BuildConfig.GET_CHORE.replace(oldValue=":choreId", newValue=id.toString()))
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")
                val responseCode = con.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }

                    val chore: Chore =
                        Gson().fromJson(response, object : TypeToken<Chore>() {}.type)
                    detailedChore.value = chore
                }

                getChoreByCIdLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun knock(choreId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            knockChoreLoading.value = true
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "choreId" to choreId
                )
                val requestBodyString = gson.toJson(requestBody)

                val url = URL(BuildConfig.KNOCK_CHORE_URL.replace(oldValue=":choreId", newValue=choreId.toString()))
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")
                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }

                    val responseCode = responseCode
                    println("Response:: $responseCode")

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = inputStream.bufferedReader().use { it.readText() }

                        val chore: Chore =
                            Gson().fromJson(response, object : TypeToken<Chore>() {}.type)
                        detailedChore.value = chore
                    }

                    knockChoreLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun completeChore(id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            completeChoreLoading.value = true
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "choreId" to id
                )
                val requestBodyString = gson.toJson(requestBody)

                val url = URL(BuildConfig.COMPLETE_CHORE_URL.replace(oldValue=":choreId", newValue=id.toString()))
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")
                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }

                    val responseCode = responseCode
                    println("Response:: $responseCode")

                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        val response = inputStream.bufferedReader().use { it.readText() }

                        val chore: Chore =
                            Gson().fromJson(response, object : TypeToken<Chore>() {}.type)
                        detailedChore.value = chore
                    }

                    completeChoreLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
