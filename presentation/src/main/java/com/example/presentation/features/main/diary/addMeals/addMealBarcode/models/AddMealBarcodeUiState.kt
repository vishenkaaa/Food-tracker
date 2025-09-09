package com.example.presentation.features.main.diary.addMeals.addMealBarcode.models

data class AddMealBarcodeUiState(
    val scannedBarcode: String? = null,
    val isFlashOn: Boolean = false,
    val isCameraReady: Boolean = false,
    val hasFlashUnit: Boolean = false,

    val inputMode: BarcodeInputMode = BarcodeInputMode.SCANNER,
    val showManualEntry: Boolean = false,
    val manualBarcodeInput: String = ""
)