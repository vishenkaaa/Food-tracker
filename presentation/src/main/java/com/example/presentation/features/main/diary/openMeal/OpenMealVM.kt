package com.example.presentation.features.main.diary.openMeal

import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.presentation.arch.BaseViewModel
import com.example.domain.extension.calculateMealNutritionForDishes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OpenMealVM @Inject constructor() : BaseViewModel() {
    private val _uiState = MutableStateFlow(OpenMealUIState())
    val uiState: StateFlow<OpenMealUIState> = _uiState.asStateFlow()

    fun initializeMeal(
        mealType: MealType,
        dishes: List<Dish>,
        date: LocalDate,
        targetCalories: Int
    ) {
        val nutritionForDishes = dishes.calculateMealNutritionForDishes()
        _uiState.update {
            it.copy(
                mealType = mealType,
                dishes = dishes,
                date = date,
                targetCalories = targetCalories,
                calories = nutritionForDishes.calories,
                carbs = nutritionForDishes.carb,
                protein = nutritionForDishes.protein,
                fat = nutritionForDishes.fat
            )
        }
    }

    fun onEditDish(dish: Dish) {
        // TODO: Open edit
    }

    fun onRemoveDish(dish: Dish) {
        val updatedDishes = _uiState.value.dishes.filter { it.id != dish.id }
        val nutritionForDishes = updatedDishes.calculateMealNutritionForDishes()
        _uiState.update {
            it.copy(
                dishes = updatedDishes,
                calories = nutritionForDishes.calories,
                carbs = nutritionForDishes.carb,
                protein = nutritionForDishes.protein,
                fat = nutritionForDishes.fat
            )
        }
        // TODO Delete fromFirebase
    }
}

data class OpenMealUIState(
    val mealType: MealType = MealType.BREAKFAST,
    val dishes: List<Dish> = emptyList(),
    val date: LocalDate = LocalDate.now(),
    val targetCalories: Int = 0,

    val calories: Int = 0,
    val carbs: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
)