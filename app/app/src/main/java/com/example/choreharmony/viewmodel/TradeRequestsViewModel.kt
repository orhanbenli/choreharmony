package com.example.choreharmony.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choreharmony.BuildConfig
import com.example.choreharmony.model.TradeRequest
import com.example.choreharmony.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class TradeRequestsViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {
    var pendingTradeRequests = MutableStateFlow<List<TradeRequest>>(emptyList())
    var sentTradeRequests = MutableStateFlow<List<TradeRequest>>(emptyList())

    var getPendingTradeRequestsLoading = mutableStateOf(false)
    var getSentTradeRequestsLoading = mutableStateOf(false)
    var deleteSentRequestLoading = mutableStateOf(false)
    var managePendingRequestsLoading = mutableStateOf(false)

    fun getPendingTradeRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            getPendingTradeRequestsLoading.value = true
            try {
                val obj = URL(BuildConfig.PENDING_TRADE_REQUESTS_URL)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")
                val responseCode = con.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val tradeRequests: List<TradeRequest> =
                        Gson().fromJson(response, object : TypeToken<List<TradeRequest>>() {}.type)
                    pendingTradeRequests.value = tradeRequests
                }

                getPendingTradeRequestsLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getSentTradeRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            getSentTradeRequestsLoading.value = true
            try {
                val obj = URL(BuildConfig.SENT_TRADE_REQUESTS_URL)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")
                val responseCode = con.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val tradeRequests: List<TradeRequest> =
                        Gson().fromJson(response, object : TypeToken<List<TradeRequest>>() {}.type)
                    sentTradeRequests.value = tradeRequests
                }

                getSentTradeRequestsLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteSentTradeRequest(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteSentRequestLoading.value = true
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "trade_id" to id
                )
                val requestBodyString = gson.toJson(requestBody)

                val url = URL(BuildConfig.SENT_TRADE_REQUESTS_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "DELETE"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = inputStream.bufferedReader().use { it.readText() }
                        val tradeRequests: List<TradeRequest> =
                            Gson().fromJson(response, object : TypeToken<List<TradeRequest>>() {}.type)
                        sentTradeRequests.value = tradeRequests
                    }

                    deleteSentRequestLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun managePendingTradeRequest(id: Int, approval: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            getPendingTradeRequestsLoading.value = true
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "trade_id" to id,
                    "approval" to approval
                )
                val requestBodyString = gson.toJson(requestBody)

                val url = URL(BuildConfig.PENDING_TRADE_REQUESTS_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }
                    val responseCode = responseCode
                    println(responseCode)
                    getPendingTradeRequests()
                    managePendingRequestsLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}