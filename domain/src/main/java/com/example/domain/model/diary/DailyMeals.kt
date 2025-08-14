package com.example.domain.model.diary

data class DailyMeals(
    val breakfast: List<Dish> = emptyList(),
    val lunch: List<Dish> = emptyList(),
    val dinner: List<Dish> = emptyList(),
    val snacks: List<Dish> = emptyList()
)