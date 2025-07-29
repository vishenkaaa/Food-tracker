package com.example.domain.model

import java.time.LocalDate
import java.time.Period

data class User(
    val id: String = "",
    val name: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val isNew: Boolean = false,
    val goal: Goal = Goal.MAINTAIN,
    val currentWeight: Float? = null,
    val weightChange: Float? = null,
    val height: Int? = null,
    val gender: Gender = Gender.MALE,
    val birthDate: LocalDate? = null,
    val userActivityLevel: UserActivityLevel = UserActivityLevel.SEDENTARY,
    val targetCalories: Int = 0
){
    fun calculateCalories(): Int? {
        if(currentWeight == null || height == null || birthDate == null)
            return null

        val age = Period.between(birthDate, LocalDate.now()).years

        val bmr = when (gender) {
            Gender.MALE -> 10 * currentWeight + 6.25 * height - 5 * age + 5
            Gender.FEMALE -> 10 * currentWeight + 6.25 * height - 5 * age - 161
        }

        val maintenance = bmr * userActivityLevel.factor

        return when (goal) {
            Goal.LOSE -> (maintenance - 300).toInt()
            Goal.GAIN -> (maintenance + 300).toInt()
            Goal.MAINTAIN -> maintenance.toInt()
        }
    }
}
