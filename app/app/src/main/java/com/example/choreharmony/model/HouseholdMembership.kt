package com.example.choreharmony.model

data class HouseholdMembership(
    var id: Int,
    var user_id: Int,
    var household_id: Int,
    var household: Household,
    var user: ExternalUser,
    var pending_flag: Boolean
)