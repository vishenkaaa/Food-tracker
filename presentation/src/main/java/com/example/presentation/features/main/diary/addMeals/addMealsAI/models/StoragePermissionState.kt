package com.example.presentation.features.main.diary.addMeals.addMealsAI.models

data class StoragePermissionState(
    val hasPermission: Boolean = false,
    val shouldRequest: Boolean = false,
    val shouldOpenGallery: Boolean = false,
    val permanentlyDenied: Boolean = false,
    val showPermanentlyDeniedDialog: Boolean = false
)