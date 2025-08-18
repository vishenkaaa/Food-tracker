package com.example.domain.repository

interface CameraRepository {
    fun hasCameraPermission(): Boolean
    fun hasStoragePermission(): Boolean
    fun hasAllPermissions(): Boolean
    fun getMissingPermissions(): List<String>
    fun isCameraAvailable(): Boolean
    suspend fun capturePhoto(): Result<String>
    fun hasFlashUnit(): Boolean
    fun toggleFlash(): Result<Unit>
    fun releaseCamera()
}