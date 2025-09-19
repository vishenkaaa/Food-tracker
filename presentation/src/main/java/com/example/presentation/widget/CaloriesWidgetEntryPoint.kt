package com.example.presentation.widget

import com.example.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.domain.usecase.meal.GetCaloriesProgressUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CaloriesWidgetEntryPoint {
    fun getCaloriesUseCase(): GetCaloriesProgressUseCase
    fun getCurrentUserIdUseCase(): GetCurrentUserIdUseCase
}