package com.example.domain.model.diary

enum class MealType(val value: String) {
    BREAKFAST("breakfast"),
    LUNCH("lunch"),
    DINNER("dinner"),
    SNACKS("snacks");

    companion object {
        fun fromValue(value: String): MealType? =
            entries.find { it.value.equals(value, ignoreCase = true) }
    }
}