package com.example.domain.model.diary

import kotlinx.serialization.Serializable

@Serializable
enum class UnitType(val value: String) {
    GRAM("g"),
    MILLILITER("ml"),
    LITER("l"),
    PIECE("piece");

    companion object {
        fun fromValue(value: String): UnitType =
            entries.find { it.value.equals(value, ignoreCase = true) } ?: GRAM
    }
}
