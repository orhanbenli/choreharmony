package com.example.choreharmony.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choreharmony.BuildConfig
import com.example.choreharmony.model.UserDetails
import com.example.choreharmony.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {
    var getUserDetailsLoading = mutableStateOf(false)

    var userDetails = mutableStateOf<UserDetails?>(null)

    fun getUserDetails(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserDetailsLoading.value = true
            try {
                val obj = URL(BuildConfig.GET_USER_DETAILS_URL.replace(oldValue = ":userId", newValue = userId.toString()))
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")
                val responseCode = con.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    println(response)
                    val details: UserDetails =
                        Gson().fromJson(response, object : TypeToken<UserDetails>() {}.type)
                    userDetails.value = details
                }

                getUserDetailsLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}