package com.example.choreharmony.model

data class UserDetails(
    val id: Int,
    val email: String,
    val first_name: String,
    val last_name: String,
    val household_power: Int,
    val reviews: List<Review>
)