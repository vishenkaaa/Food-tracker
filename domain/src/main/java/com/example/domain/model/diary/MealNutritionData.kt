package com.example.domain.model.diary

data class MealNutritionData(
    val breakfast: NutritionData = NutritionData(),
    val lunch: NutritionData = NutritionData(),
    val dinner: NutritionData = NutritionData(),
    val snacks: NutritionData = NutritionData()
)