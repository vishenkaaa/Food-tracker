package com.example.domain.model

enum class Gender(val value: String) {
    MALE("male"),
    FEMALE("female");

    companion object{
        fun fromValue(value: String): Gender? {
            return entries.find { it.value == value }
        }
    }
}