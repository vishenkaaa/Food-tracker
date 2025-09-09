package com.example.domain.model.diary

import kotlinx.serialization.Serializable

@Serializable
enum class MealType(val value: String) {
    BREAKFAST("breakfast"),
    LUNCH("lunch"),
    SNACKS("snacks"),
    DINNER("dinner");

    companion object {
        fun fromValue(value: String): MealType? =
            entries.find { it.value.equals(value, ignoreCase = true) }
    }
}