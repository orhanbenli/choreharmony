package com.example.choreharmony

import com.example.choreharmony.repository.UserRepository
import com.example.choreharmony.viewmodel.CreateChoreViewModel
import org.junit.Test

class CreateChoreViewModelUnitTest {
    private val userRepository = UserRepository()
    private val createChoreViewModel = CreateChoreViewModel(userRepository)

    @Test
    fun createChore_invalidName_isError() {
        createChoreViewModel.name.value = "  "

        createChoreViewModel.createChore { println("callback") }

        assert(createChoreViewModel.allValuesError.value!!.trim().isNotEmpty())
    }

    @Test
    fun createChore_invalidRecurrence_isError() {
        createChoreViewModel.name.value = "hello chore"
        createChoreViewModel.recurrenceInDays.value = -1

        createChoreViewModel.createChore { println("callback") }

        assert(createChoreViewModel.allValuesError.value!!.trim().isNotEmpty())
    }

    @Test
    fun createChore_validRequest_NoErrors() {
        createChoreViewModel.name.value = "hello chore"
        createChoreViewModel.recurrenceInDays.value = 1

        createChoreViewModel.createChore { println("callback") }

        assert(createChoreViewModel.allValuesError.value.isNullOrEmpty())
    }
}