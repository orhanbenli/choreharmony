package com.example.choreharmony

import com.example.choreharmony.repository.UserRepository
import com.example.choreharmony.viewmodel.CreateHouseholdViewModel
import org.junit.Test

class CreateHouseholdViewModelUnitTest {
    private val userRepository = UserRepository()
    private val createHouseholdViewModel = CreateHouseholdViewModel(userRepository)

    @Test
    fun createHousehold_invalidName_isError() {
        createHouseholdViewModel.name.value = "  "

        createHouseholdViewModel.createHousehold { println("callback") }

        assert(createHouseholdViewModel.error.value.trim().isNotEmpty())
    }

    @Test
    fun createChore_validRequest_NoErrors() {
        createHouseholdViewModel.name.value = "hello chore"

        createHouseholdViewModel.createHousehold { println("callback") }

        assert(createHouseholdViewModel.error.value.isEmpty())
    }
}