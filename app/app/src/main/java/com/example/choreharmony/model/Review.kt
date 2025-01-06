package com.example.choreharmony.model

data class Review(
 val id: Int,
    val reviewer_user_id: Int,
    val reviewee_user_id: Int,
    val review_comment_id: Int,
    val like: Boolean,
    val reviewer: ExternalUser,
    val reviewee: ExternalUser,
    val comment: Comment
)
