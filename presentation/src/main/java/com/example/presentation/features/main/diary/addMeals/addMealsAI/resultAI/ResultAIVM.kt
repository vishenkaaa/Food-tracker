package com.example.presentation.features.main.diary.addMeals.addMealsAI.resultAI

import android.content.Context
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.domain.usecase.meal.AddDishToMealUseCase
import com.example.domain.usecase.meal.RemoveDishFromMealUseCase
import com.example.domain.usecase.meal.UpdateDishInMealUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseOpenMealVM
import com.example.presentation.common.utils.WidgetEventNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ResultAIVM @Inject constructor(
    private val addDishToMealUseCase: AddDishToMealUseCase,
    updateDishInMealUseCase: UpdateDishInMealUseCase,
    removeDishFromMealUseCase: RemoveDishFromMealUseCase,
    getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    @ApplicationContext private val context: Context,
) : BaseOpenMealVM(
    updateDishInMealUseCase,
    removeDishFromMealUseCase,
    getCurrentUserIdUseCase,
    context
) {
    override fun onSaveEditedDish(
        updatedDish: Dish,
        updatedMealType: MealType,
        refreshDairy: () -> Unit,
        refreshStatistics: (LocalDate) -> Unit
    ) {
        updateLocalDishes(updatedDish, updatedMealType)
        onEditDishDismiss()
    }

    override fun onDeleteConfirmationResult(status: Boolean, refreshDairy: () -> Unit) {
        if (status) uiState.value.dishIdToDelete?.let { id ->
            deleteLocalDish(id)
        }
        _uiState.update {
            it.copy(
                showDeleteMealDialog = false,
                dishIdToDelete = null
            )
        }
    }

    suspend fun onSaveDishes(refreshDairy: () -> Unit): Result<Unit> {
        return try {
            val userId = getCurrentUserIdUseCase() ?: return Result.failure(Exception(context.getString(R.string.user_not_authenticated)))
            val date = uiState.value.date.toString()
            val mealType = uiState.value.mealType

            uiState.value.dishes.forEach { dish ->
                val result = addDishToMealUseCase(userId, date, mealType, dish)
                result.onFailure {
                    handleError(Exception(context.getString(R.string.saving_error)))
                    return Result.failure(Exception(context.getString(R.string.saving_error)))
                }
            }

            WidgetEventNotifier.notifyCaloriesUpdated(context)

            refreshDairy()
            Result.success(Unit)
        } catch (e: Exception) {
            handleError(e)
            Result.failure(e)
        }
    }
}