package com.example.domain.model

enum class Gender(val value: String) {
    FEMALE("female"),
    MALE("male");

    companion object{
        fun fromValue(value: String): Gender? {
            return entries.find { it.value == value }
        }
    }
}