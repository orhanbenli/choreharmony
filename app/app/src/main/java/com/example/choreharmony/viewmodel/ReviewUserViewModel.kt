package com.example.choreharmony.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choreharmony.BuildConfig
import com.example.choreharmony.model.Comment
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
class ReviewUserViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {
    var getCommentsLoading = mutableStateOf(false)
    var postReviewLoading = mutableStateOf(false)
    var like = mutableStateOf<Boolean?>(null)
    var comments = MutableStateFlow<List<Comment>>(emptyList())

    var allErrorsValue = mutableStateOf("")
    var comment = mutableStateOf<Comment?>(null)

    fun getAllComments() {
        viewModelScope.launch(Dispatchers.IO) {
            getCommentsLoading.value = true
            try {
                val obj = URL(BuildConfig.GET_ALL_COMMENTS_URL)
                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")
                val responseCode = con.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = con.inputStream.bufferedReader().use { it.readText() }
                    val allComments: List<Comment> =
                        Gson().fromJson(response, object : TypeToken<List<Comment>>() {}.type)
                    comments.value = allComments
                }

                getCommentsLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun postReview(userId: Int, callback: () -> Unit) {
        if (comment.value == null) {
            allErrorsValue.value = "You must select a comment to review this user."
            return
        }

        if (like.value == null) {
            allErrorsValue.value = "You must choose either like or dislike to review this user."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            postReviewLoading.value = true
            try {
                val gson = Gson()
                val requestBody = mapOf(
                    "reviewee_user_id" to userId,
                    "comment_id" to comment.value!!.id,
                    "like" to like.value
                )
                val requestBodyString = gson.toJson(requestBody)

                val url = URL(BuildConfig.POST_REVIEW_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Authorization", "Bearer ${userRepository.currentUser.value!!.token}")

                    OutputStreamWriter(outputStream).use { it.write(requestBodyString) }

                    val responseCode = responseCode
                    println(responseCode)
                    postReviewLoading.value = false
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