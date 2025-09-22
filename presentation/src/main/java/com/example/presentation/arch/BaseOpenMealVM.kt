package com.example.presentation.arch

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.domain.extension.calculateMealNutritionForDishes
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.domain.usecase.meal.RemoveDishFromMealUseCase
import com.example.domain.usecase.meal.UpdateDishInMealUseCase
import com.example.presentation.R
import com.example.presentation.features.main.diary.DiaryVM
import com.example.presentation.features.main.diary.openMeal.models.OpenMealUIState
import com.example.presentation.common.utils.WidgetUpdater
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

open class BaseOpenMealVM (
    protected val updateDishInMealUseCase: UpdateDishInMealUseCase,
    protected val removeDishFromMealUseCase: RemoveDishFromMealUseCase,
    protected val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    @ApplicationContext private val context: Context,
) : BaseViewModel() {
    protected val _uiState = MutableStateFlow(OpenMealUIState())
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

    open fun onSaveEditedDish(updatedDish: Dish, updatedMealType: MealType, diaryVM: DiaryVM) {
        viewModelScope.launch {
            handleLoading(true)

            try {
                val userId = getCurrentUserIdUseCase()
                    ?: throw Exception(context.getString(R.string.user_not_authenticated))

                updateDishInMealUseCase(
                    userId = userId,
                    date = uiState.value.date.toString(),
                    originalMealType = uiState.value.mealType,
                    newMealType = updatedMealType,
                    dish = updatedDish
                ).fold(
                    onSuccess = {
                        updateLocalDishes(updatedDish, updatedMealType)
                        WidgetUpdater.updateWidget(context)
                        diaryVM.refreshData()
                        onEditDishDismiss()
                    },
                    onFailure = {
                        handleError(Exception(context.getString(R.string.failed_to_update_dish)), context)
                    }
                )
            } catch (e: Exception) {
                handleError(e, context)
            } finally {
                handleLoading(false)
            }
        }
    }

    protected fun updateLocalDishes(updatedDish: Dish, updatedMealType: MealType) {
        val updatedDishes = if (updatedMealType != uiState.value.mealType) {
            _uiState.value.dishes.filter { it.id != updatedDish.id }
        } else {
            _uiState.value.dishes.map { dish ->
                if (dish.id == updatedDish.id) updatedDish else dish
            }
        }

        updateNutritionState(updatedDishes)
    }

    fun requestDeleteConfirmation(dishId: String) {
        _uiState.update {
            it.copy(
                showDeleteMealDialog = true,
                dishIdToDelete = dishId
            )
        }
    }

    open fun onDeleteConfirmationResult(status: Boolean, diaryVM: DiaryVM) {
        _uiState.update {
            it.copy(
                showDeleteMealDialog = false,
                dishIdToDelete = null
            )
        }
        if (status) uiState.value.dishIdToDelete?.let { id ->
            onDeleteDish(id, diaryVM)
        }
    }

    private fun onDeleteDish(dishId: String, diaryVM: DiaryVM) {
        viewModelScope.launch {
            handleLoading(true)

            try {
                val userId = getCurrentUserIdUseCase()
                    ?: throw Exception(context.getString(R.string.user_not_authenticated))

                removeDishFromMealUseCase(
                    userId,
                    uiState.value.date.toString(),
                    uiState.value.mealType,
                    dishId
                ).fold(
                    onSuccess = {
                        deleteLocalDish(dishId)
                        diaryVM.refreshData()
                    },
                    onFailure = {
                        handleError(Exception(context.getString(R.string.failed_to_remove_the_dish)), context)
                    }
                )
            } catch (e: Exception) {
                handleError(e, context)
            } finally {
                handleLoading(false)
            }
        }
    }

    protected fun deleteLocalDish(dishId: String) {
        val updatedDishes = _uiState.value.dishes.filter { it.id != dishId }
        updateNutritionState(updatedDishes)
    }

    private fun updateNutritionState(dishes: List<Dish>) {
        val nutritionForDishes = dishes.calculateMealNutritionForDishes()
        _uiState.update {
            it.copy(
                dishes = dishes,
                calories = nutritionForDishes.calories,
                carbs = nutritionForDishes.carb,
                protein = nutritionForDishes.protein,
                fat = nutritionForDishes.fat
            )
        }
    }
}