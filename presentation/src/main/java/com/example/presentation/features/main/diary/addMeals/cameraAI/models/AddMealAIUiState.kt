package com.example.presentation.features.main.diary.addMeals.cameraAI.models

data class AddMealAIUiState(
    val isCameraReady: Boolean = false,
    val capturedPhotoUri: String? = null,
    val isFlashOn: Boolean = false,
    val hasFlashUnit: Boolean = false
)