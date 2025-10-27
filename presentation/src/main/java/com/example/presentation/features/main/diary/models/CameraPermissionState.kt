package com.example.presentation.features.main.diary.models

import com.example.domain.model.diary.MealType

data class CameraPermissionState(
    val hasPermission: Boolean = false,
    val shouldRequest: Boolean = false,
    val permanentlyDenied: Boolean = false,
    val pendingMealType: MealType? = null,
    val showPermanentlyDeniedDialog: Boolean = false
)