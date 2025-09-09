package com.example.presentation.features.main.diary.openMeal.models

import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import java.time.LocalDate

data class OpenMealUIState(
    val showEditDishDialog: Boolean = false,
    val dishToEdit: Dish? = null,

    val showDeleteMealDialog: Boolean = false,
    val dishIdToDelete: String? = null,

    val mealType: MealType = MealType.BREAKFAST,
    val dishes: List<Dish> = emptyList(),
    val date: LocalDate = LocalDate.now(),
    val targetCalories: Int = 0,

    val calories: Int = 0,
    val carbs: Float = 0f,
    val protein: Float = 0f,
    val fat: Float = 0f,
)