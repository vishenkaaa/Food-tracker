package com.example.domain.model.diary

import kotlinx.serialization.Serializable

@Serializable
enum class UnitType(val value: String) {
    GRAM("g"),
    MILLILITER("ml"),
    LITER("l"),
    PIECE("pcs");

    companion object {
        fun fromValue(value: String): UnitType? =
            entries.find { it.value.equals(value, ignoreCase = true) }
    }
}
