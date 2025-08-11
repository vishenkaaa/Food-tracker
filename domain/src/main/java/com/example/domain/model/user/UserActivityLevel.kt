package com.example.domain.model.user

enum class UserActivityLevel(val value: String, val factor: Double) {
    SEDENTARY("sedentary", 1.2),
    LIGHTLY_ACTIVE("lightActive", 1.375),
    ACTIVE("active", 1.55),
    VERY_ACTIVE("veryActive", 1.725);

    companion object{
        fun fromValue(value: String): UserActivityLevel? {
            return UserActivityLevel.entries.find { it.value == value }
        }
    }
}