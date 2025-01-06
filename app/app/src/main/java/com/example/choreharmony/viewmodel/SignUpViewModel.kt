package com.example.choreharmony.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.choreharmony.BuildConfig
import com.example.choreharmony.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    val userRepository: UserRepository,
) : ViewModel() {
    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var id = mutableIntStateOf(-1)
    var passwordError = mutableStateOf<String?>(null)
    var allValuesError = mutableStateOf<String?>(null)

    fun createUser() {
        userRepository.createUser(firstName, lastName, email, password, id)
    }

    fun validatePasswordAndSignUp(password: String) {
        if (!isValidPassword(password)) {
            passwordError.value =
                "Please ensure password is 8 characters long, contains one symbol, one number, and has both uppercase and lowercase letters."
        } else {
            passwordError.value = null
        }
    }

    fun areAllValuesFilled(firstName: String, lastName: String, email: String, password: String) {
        if (firstName != "" && lastName != "" && email != "" && password != "") {
            allValuesError.value = null
        } else {
            allValuesError.value = "Please fill in all values."
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    fun sendVerificationEmail(callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var emailSent = false
            try {
                val url = URL(BuildConfig.EMAIL_VERIFICATION_URL)
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")

                    val body = """{"email": "${email.value}"}"""

                    OutputStreamWriter(outputStream).use { it.write(body) }
                    emailSent = responseCode == HttpURLConnection.HTTP_OK
                    if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                        allValuesError.value = "Please enter an email"
                    } else if (responseCode == HttpURLConnection.HTTP_CONFLICT) {
                        allValuesError.value = "This account already exists"
                    } else if (!emailSent) {
                        allValuesError.value = "The account could not be created"
                    }
                }
            } catch (e: Exception) {
                allValuesError.value = "An unexpected error occurred"
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                callback(emailSent)
            }
        }
    }
}