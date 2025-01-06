package com.example.choreharmony.model

data class Notification(
    var id: Int,
    var destination_user_id: Int,
    var destination_user: ExternalUser,
    var notification_type: String,
    var navigator_id: Int?,
    var content: String,
    var create_date: String
)