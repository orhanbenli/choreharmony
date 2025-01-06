package com.example.choreharmony.model

data class TradeRequest (
    val id: Int,
    val source_user_id: Int,
    val source_user: ExternalUser,
    val destination_user_id: Int,
    val destination_user: ExternalUser,
    val chore_id: Int,
    val chore: Chore,
    val household_power: Int
)