package com.example.choreharmony

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.example.choreharmony.repository.UserRepository
import com.example.choreharmony.viewmodel.EmailVerificationViewModel
import org.junit.Test

class EmailVerificationViewModelUnitTest {
    private val userRepository = UserRepository()

    @Test
    fun emailVerificationViewModel_createRequest_returnsJsonRequest() {
        userRepository.createUser(
            mutableStateOf("KESHAV"),
            mutableStateOf("GUPTA"),
            mutableStateOf("thisismyemail"),
            mutableStateOf("pwdpwdpwd"),
            mutableIntStateOf(10122)
        )

        val emailVerificationViewModel = EmailVerificationViewModel(userRepository)

        val request = emailVerificationViewModel.getRegistrationRequest()

        assert(request.contains("KESHAV"))
        assert(request.contains("GUPTA"))
        assert(request.contains("thisismyemail"))
        assert(request.contains("pwdpwdpwd"))
    }
}