package com.example.choreharmony.model

data class HouseholdChat(
    var id: Int,
    var user_id: Int,
    var household_id: Int,
    var message: String,
    var create_date: String,
    var user: ExternalUser
)