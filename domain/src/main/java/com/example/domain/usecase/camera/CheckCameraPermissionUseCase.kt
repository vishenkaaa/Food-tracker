package com.example.domain.usecase.camera

import com.example.domain.repository.CameraRepository
import javax.inject.Inject

class CheckCameraPermissionUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) {
    fun hasCameraPermission(): Boolean = cameraRepository.hasCameraPermission()
    fun hasStoragePermission(): Boolean = cameraRepository.hasStoragePermission()
    fun hasAllPermissions(): Boolean = cameraRepository.hasAllPermissions()
    fun getMissingPermissions(): List<String> = cameraRepository.getMissingPermissions()
    fun isCameraAvailable(): Boolean = cameraRepository.isCameraAvailable()
}