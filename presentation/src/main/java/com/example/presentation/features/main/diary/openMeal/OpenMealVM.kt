package com.example.presentation.features.main.diary.openMeal

import android.content.Context
import com.example.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.domain.usecase.meal.RemoveDishFromMealUseCase
import com.example.domain.usecase.meal.UpdateDishInMealUseCase
import com.example.presentation.arch.BaseOpenMealVM
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class OpenMealVM @Inject constructor(
    updateDishInMealUseCase: UpdateDishInMealUseCase,
    removeDishFromMealUseCase: RemoveDishFromMealUseCase,
    getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    @ApplicationContext context: Context,
) : BaseOpenMealVM(
    updateDishInMealUseCase,
    removeDishFromMealUseCase,
    getCurrentUserIdUseCase,
    context
)