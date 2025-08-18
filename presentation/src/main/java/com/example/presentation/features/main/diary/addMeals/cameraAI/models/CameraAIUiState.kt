package com.example.presentation.features.main.diary.addMeals.cameraAI.models

import android.net.Uri

data class CameraAIUiState(
    val error: String? = null,
    val hasPermissions: Boolean = false,
    val isCameraReady: Boolean = false,
    val capturedPhotoUri: Uri? = null,
    val isFlashOn: Boolean = false,
    val hasFlashUnit: Boolean = false
)