package com.example.presentation.features.main.diary.addMeals.addMealsAI.resultAI

import android.content.Context
import android.util.Log
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.domain.usecase.meal.AddDishToMealUseCase
import com.example.domain.usecase.meal.RemoveDishFromMealUseCase
import com.example.domain.usecase.meal.UpdateDishInMealUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseOpenMealVM
import com.example.presentation.features.main.diary.DiaryVM
import com.example.presentation.common.utils.WidgetUpdater
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.update
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
    override fun onSaveEditedDish(updatedDish: Dish, updatedMealType: MealType, diaryVM: DiaryVM) {
        updateLocalDishes(updatedDish, updatedMealType)
        onEditDishDismiss()
    }

    override fun onDeleteConfirmationResult(status: Boolean, diaryVM: DiaryVM) {
        _uiState.update {
            it.copy(
                showDeleteMealDialog = false,
                dishIdToDelete = null
            )
        }
        if (status) uiState.value.dishIdToDelete?.let { id ->
            deleteLocalDish(id)
        }
    }

    suspend fun onSaveDishes(diaryVM: DiaryVM): Result<Unit> {
        return try {
            val userId = getCurrentUserIdUseCase() ?: return Result.failure(Exception(context.getString(R.string.user_not_authenticated)))
            val date = uiState.value.date.toString()
            val mealType = uiState.value.mealType

            uiState.value.dishes.forEach { dish ->
                val result = addDishToMealUseCase(userId, date, mealType, dish)
                result.onFailure { error ->
                    Log.e("ResultAIVM", "Failed to save dish: ${dish.title}", error)
                    handleError(Exception(context.getString(R.string.saving_error)))
                    return Result.failure(Exception(context.getString(R.string.saving_error)))
                }
            }

            WidgetUpdater.updateWidget(context)
            diaryVM.refreshData()
            Result.success(Unit)
        } catch (e: Exception) {
            handleError(e)
            Result.failure(e)
        }
    }
}