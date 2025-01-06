package com.example.choreharmony

import androidx.compose.runtime.mutableStateOf
import com.example.choreharmony.model.ExternalUser
import com.example.choreharmony.model.Household
import com.example.choreharmony.model.HouseholdChat
import com.example.choreharmony.repository.UserRepository
import com.example.choreharmony.viewmodel.HouseholdChatViewModel
import org.junit.Test

class HouseholdChatViewModelUnitTest {
    private val userRepository = UserRepository()
    private val householdChatViewModel = HouseholdChatViewModel(userRepository)
    private val externalUser = ExternalUser(10122, "asdf", "asdf", "email")

    @Test
    fun isMessageSenderCurrentUser_returnsTrue() {
        userRepository.createUser(mutableStateOf("keshav"),
            mutableStateOf("gupta"),
            mutableStateOf("asdf"),
            mutableStateOf("asdfaf"),
            mutableStateOf(101))

        val sentByUser = householdChatViewModel.isMessageSentByUser(HouseholdChat(1,
            101,
            2,
            "asdf",
            create_date = "asdf",
            user = externalUser
        ))

        assert(sentByUser)
    }

    @Test
    fun isMessageSenderCurrentUser_returnsFalse() {
        userRepository.createUser(mutableStateOf("keshav"),
            mutableStateOf("gupta"),
            mutableStateOf("asdf"),
            mutableStateOf("asdfaf"),
            mutableStateOf(101))

        val sentByUser = householdChatViewModel.isMessageSentByUser(HouseholdChat(1,
            10122,
            2,
            "asdf",
            create_date = "asdf",
            user = externalUser
        ))

        assert(!sentByUser)
    }

    @Test
    fun isHouseholdOwnerCurrentUser_returnsTrue() {
        userRepository.createUser(mutableStateOf("keshav"),
            mutableStateOf("gupta"),
            mutableStateOf("asdf"),
            mutableStateOf("asdfaf"),
            mutableStateOf(101))

        householdChatViewModel.household.value = Household(1,
            "name",
            "code",
            101,
            externalUser,
            members = listOf())

        val currentUserIsOwner = householdChatViewModel.isUserHouseholdOwner()

        assert(currentUserIsOwner)
    }

    @Test
    fun isHouseholdOwnerCurrentUser_returnsFalse() {
        userRepository.createUser(mutableStateOf("keshav"),
            mutableStateOf("gupta"),
            mutableStateOf("asdf"),
            mutableStateOf("asdfaf"),
            mutableStateOf(123324))

        householdChatViewModel.household.value = Household(1,
            "name",
            "code",
            101,
            externalUser,
            members = listOf())

        val currentUserIsOwner = householdChatViewModel.isUserHouseholdOwner()

        assert(!currentUserIsOwner)
    }
}
