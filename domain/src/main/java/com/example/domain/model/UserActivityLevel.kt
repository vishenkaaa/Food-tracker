package com.example.domain.model

enum class UserActivityLevel(val factor: Double) {
    SEDENTARY(1.2),
    LIGHTLY_ACTIVE(1.375),
    ACTIVE(1.55),
    VERY_ACTIVE(1.725)
}