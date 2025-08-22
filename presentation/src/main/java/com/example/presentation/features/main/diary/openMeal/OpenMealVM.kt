package com.example.presentation.features.main.diary.openMeal

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.presentation.arch.BaseViewModel
import com.example.domain.extension.calculateMealNutritionForDishes
import com.example.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.domain.usecase.meal.RemoveDishFromMealUseCase
import com.example.domain.usecase.meal.UpdateDishInMealUseCase
import com.example.presentation.R
import com.example.presentation.features.main.diary.DiaryVM
import com.example.presentation.features.main.diary.openMeal.models.OpenMealUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OpenMealVM @Inject constructor(
    private val updateDishInMealUseCase: UpdateDishInMealUseCase,
    private val removeDishFromMealUseCase: RemoveDishFromMealUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    @ApplicationContext private val context: Context,
) : BaseViewModel() {
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
        _uiState.update {
            it.copy(
                showEditDishDialog = true,
                dishToEdit = dish
            )
        }
    }

    fun onEditDishDismiss() {
        _uiState.update {
            it.copy(
                showEditDishDialog = false,
                dishToEdit = null
            )
        }
    }

    fun onSaveEditedDish(updatedDish: Dish, updatedMealType: MealType,  diaryVM: DiaryVM) {
        viewModelScope.launch {
            handleLoading(true)
            val userId = getCurrentUserIdUseCase()
            if (userId == null) {
                handleError(Exception(context.getString(R.string.user_not_authenticated)))
                handleLoading(false)
                return@launch
            }

            updateDishInMealUseCase(
                userId = userId,
                date = uiState.value.date.toString(),
                originalMealType = uiState.value.mealType,
                newMealType = updatedMealType,
                dish = updatedDish
            ).fold(
                onSuccess = {
                    updateLocalDishes(updatedDish, updatedMealType)
                    diaryVM.refreshData()
                    onEditDishDismiss()
                    handleLoading(false)
                },
                onFailure = {
                    handleError(Exception(context.getString(R.string.failed_to_update_dish)), context)
                    handleLoading(false)
                }
            )
        }
    }

    private fun updateLocalDishes(updatedDish: Dish, updatedMealType: MealType,) {
        val updatedDishes = _uiState.value.dishes.map { dish ->
            if (dish.id == updatedDish.id) updatedDish else dish
        }
        val nutritionForDishes = updatedDishes.calculateMealNutritionForDishes()

        _uiState.update {
            it.copy(
                mealType = updatedMealType,
                dishes = updatedDishes,
                calories = nutritionForDishes.calories,
                carbs = nutritionForDishes.carb,
                protein = nutritionForDishes.protein,
                fat = nutritionForDishes.fat
            )
        }
    }

    fun requestDeleteConfirmation(dishId: String) {
        _uiState.update {
            it.copy(
                showDeleteMealDialog = true,
                dishIdToDelete = dishId
            )
        }
    }

    fun onDeleteConfirmationResult(status: Boolean, diaryVM: DiaryVM) {
        _uiState.update {
            it.copy(
                showDeleteMealDialog = false,
            )
        }
        if (status) uiState.value.dishIdToDelete?.let { id -> onDeleteDish(id, diaryVM) }
    }

    private fun onDeleteDish(dishId: String, diaryVM: DiaryVM) {
        viewModelScope.launch {
            handleLoading(true)
            val userId = getCurrentUserIdUseCase()
            if (userId == null) {
                handleError(Exception(context.getString(R.string.user_not_authenticated)))
                handleLoading(false)
                return@launch
            }

            removeDishFromMealUseCase(userId, uiState.value.date.toString(), uiState.value.mealType, dishId).fold(
                onSuccess = {
                    updateDishes(dishId = dishId, diaryVM = diaryVM)
                },
                onFailure = {
                    handleError(Exception(context.getString(R.string.failed_to_remove_the_dish)), context)
                }
            )
        }

        val updatedDishes = _uiState.value.dishes.filter { it.id != dishId }
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

        diaryVM.refreshData()
    }

    private fun updateDishes(dishId: String, diaryVM: DiaryVM){
        val updatedDishes = _uiState.value.dishes.filter { it.id != dishId }
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

        diaryVM.refreshData()
    }
}