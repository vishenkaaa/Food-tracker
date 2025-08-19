package com.example.presentation.features.main.diary.addMeals.cameraAI.models

import android.net.Uri

data class AddMealAIUiState(
    val isCameraReady: Boolean = false,
    val capturedPhotoUri: Uri? = null,
    val isFlashOn: Boolean = false,
    val hasFlashUnit: Boolean = false
)