package com.example.choreharmony

import com.example.choreharmony.repository.UserRepository
import com.example.choreharmony.viewmodel.SignUpViewModel
import org.junit.Test

class SignUpViewModelUnitTest {
    private val userRepository = UserRepository()
    private val signUpViewModel = SignUpViewModel(userRepository)

    @Test
    fun validatePassword_emptyPassword_isError() {
        signUpViewModel.validatePasswordAndSignUp("  ")

        assert(signUpViewModel.passwordError.value!!.isNotEmpty())
    }

    @Test
    fun validatePassword_tooShortPassword_isError() {
        signUpViewModel.validatePasswordAndSignUp("1234567")

        assert(signUpViewModel.passwordError.value!!.isNotEmpty())
    }

    @Test
    fun validatePassword_noNumbers_isError() {
        signUpViewModel.validatePasswordAndSignUp("Asdf&asdf")

        assert(signUpViewModel.passwordError.value!!.isNotEmpty())
    }

    @Test
    fun validatePassword_noUppercase_isError() {
        signUpViewModel.validatePasswordAndSignUp("1sdf&asdf")

        assert(signUpViewModel.passwordError.value!!.isNotEmpty())
    }

    @Test
    fun validatePassword_noLowercase_isError() {
        signUpViewModel.validatePasswordAndSignUp("1SDF&ASDF")

        assert(signUpViewModel.passwordError.value!!.isNotEmpty())
    }

    @Test
    fun validatePassword_noSymbols_isError() {
        signUpViewModel.validatePasswordAndSignUp("1SDFddASDF")

        assert(signUpViewModel.passwordError.value!!.isNotEmpty())
    }

    @Test
    fun validatePassword_validPassword_noErrors() {
        signUpViewModel.validatePasswordAndSignUp("Hello123$")

        assert(signUpViewModel.passwordError.value.isNullOrEmpty())
    }
}