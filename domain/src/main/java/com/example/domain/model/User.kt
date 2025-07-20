package com.example.domain.model

data class User(
    val id: String = "",
    val name: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val isNew: Boolean = false,
    val targetCalories: Int = 0
)