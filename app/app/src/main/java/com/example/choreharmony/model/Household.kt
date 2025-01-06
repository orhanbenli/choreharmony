package com.example.choreharmony.model

data class Household(
    var id: Int,
    var name: String,
    var join_code: String,
    var owner_id: Int,
    var owner: ExternalUser,
    var members: List<HouseholdMembership>)