package com.example.presentation.features.main.diary.addMeals.addMealsAI.models

data class AddMealAIUiState(
    val isCameraReady: Boolean = false,
    val capturedPhotoUri: String? = null,
    val isFlashOn: Boolean = false,
    val hasFlashUnit: Boolean = false
)