package com.example.domain.model.diary

import java.time.LocalDate

data class DailyMeals(
    val date: LocalDate,
    val breakfast: List<Dish> = emptyList(),
    val lunch: List<Dish> = emptyList(),
    val dinner: List<Dish> = emptyList(),
    val snacks: List<Dish> = emptyList()
)