package com.example.presentation.features.main.diary.addMeals.addMealsAI.resultAI

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
import com.example.presentation.arch.BaseOpenMealVM
import com.example.presentation.features.main.diary.DiaryVM
import com.example.presentation.features.main.diary.openMeal.OpenMealVM
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
class ResultAIVM @Inject constructor(
    updateDishInMealUseCase: UpdateDishInMealUseCase,
    removeDishFromMealUseCase: RemoveDishFromMealUseCase,
    getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    @ApplicationContext context: Context,
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

    fun onSaveDishes(diaryVM: DiaryVM){
        //TODO зберігати в файрбейс

        diaryVM.refreshData()
    }
}