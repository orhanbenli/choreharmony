package com.example.choreharmony

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.example.choreharmony.repository.UserRepository
import com.example.choreharmony.viewmodel.LoginViewModel
import org.junit.Test

class LoginViewModelUnitTest {
    private val userRepository = UserRepository()
    private val loginViewModel = LoginViewModel(userRepository)

    @Test
    fun isUserLoggedIn_NoUser_ReturnsFalse() {
        userRepository.resetUser()

        val userIsLoggedIn = loginViewModel.userIsLoggedIn()

        assert(!userIsLoggedIn)
    }

    @Test
    fun isUserLoggedIn_validToken_NoErrors() {
        userRepository.createUser(
            mutableStateOf("KESHAV"),
            mutableStateOf("GUPTA"),
            mutableStateOf("thisismyemail"),
            mutableStateOf("pwdpwdpwd"),
            mutableIntStateOf(10122)
        )
        // This is a dummy token
        userRepository.setUserToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTAsImVtYWlsIjoiazQ0Z3VwdGFAdXdhdGVybG9vLmNhIiwicGFzc3dvcmQiOiIkMmIkMTAkTkJDbUM4elo5NzNsdDljaHd5RllKZTA5NXQweDUuelYwTWZKNTFzdHlmQy9EWFU3YzNWMm0iLCJmaXJzdF9uYW1lIjoiS2VzaGF2IiwibGFzdF9uYW1lIjoiR3VwdGEiLCJpYXQiOjE3Mjk0MzYzMDV9.eo-Ng4-nZErya6au2wNbeeVJnpcI25HBBAK5Eboucdg")

        val userIsLoggedIn = loginViewModel.userIsLoggedIn()

        assert(userIsLoggedIn)
    }

    @Test
    fun areValuesFilled_BlankValues_ReturnsError() {
        loginViewModel.areAllValuesFilled("  ", "    ")
        loginViewModel.allValuesError.value!!.isNotEmpty()
    }

    @Test
    fun areValuesFilled_SomeBlankValues_ReturnsErrors() {
        loginViewModel.areAllValuesFilled(" asdf ", "    ")
        loginViewModel.allValuesError.value!!.isNotEmpty()
    }

    @Test
    fun areValuesFilled_AllFilled_NoErrors() {
        loginViewModel.areAllValuesFilled(" asdf ", "  asdfaasda  ")
        loginViewModel.allValuesError.value.isNullOrEmpty()
    }

    @Test
    fun areValuesFilled_PasswordFilled_ReturnsErrors() {
        loginViewModel.areAllValuesFilled("  ", "  asdfaasda  ")
        loginViewModel.allValuesError.value!!.isNotEmpty()
    }
}
