package com.example.choreharmony.model

data class Chore(
    var id: Int,
    var name: String,
    var recurrence_in_days: Int?,
    var completion_date: String?,
    var household: Household,
    var household_id: Int,
    var assigned_user_id: Int?,
    var assigned_user: ExternalUser?,
    var last_reminder_date: String?
)