package com.example.domain.model

enum class Goal(val value: String) {
    LOSE("lose"),
    MAINTAIN("maintain"),
    GAIN("gain");

    companion object {
        fun fromValue(value: String): Goal? {
            return entries.find { it.value == value }
        }
    }
}