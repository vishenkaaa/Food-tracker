package com.example.domain.model

data class User(
    val id: String,
    val name: String?,
    val email: String?,
    val photoUrl: String?,
    val isNew: Boolean,
    val targetCalories: Int = 0
)