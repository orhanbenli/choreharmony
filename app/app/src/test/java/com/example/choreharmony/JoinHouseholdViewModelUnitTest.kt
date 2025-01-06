package com.example.choreharmony

import com.example.choreharmony.repository.UserRepository
import com.example.choreharmony.viewmodel.JoinHouseholdViewModel
import org.junit.Test

class JoinHouseholdViewModelUnitTest {
    private val userRepository = UserRepository()
    private val joinHouseholdViewModel = JoinHouseholdViewModel(userRepository)

    @Test
    fun createChore_invalidCode_isError() {
        joinHouseholdViewModel.joinCode.value = "  "

        joinHouseholdViewModel.sendJoinRequest()

        assert(joinHouseholdViewModel.error.value.trim().isNotBlank())
    }

    @Test
    fun createChore_validRequest_NoErrors() {
        joinHouseholdViewModel.joinCode.value = "code-code"

        joinHouseholdViewModel.sendJoinRequest()

        assert(joinHouseholdViewModel.error.value.isEmpty())
    }
}