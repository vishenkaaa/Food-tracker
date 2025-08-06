package com.example.domain.model

enum class Goal(val value: String) {
    GAIN("gain"),
    MAINTAIN("maintain"),
    LOSE("lose");

    companion object {
        fun fromValue(value: String): Goal? {
            return entries.find { it.value == value }
        }
    }
}