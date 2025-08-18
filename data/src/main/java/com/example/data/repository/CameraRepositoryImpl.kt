package com.example.data.repository

import com.example.domain.repository.CameraRepository
import com.example.presentation.camera.CameraPermissionManager
import com.example.presentation.camera.CameraService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraRepositoryImpl @Inject constructor(
    private val cameraService: CameraService,
    private val cameraPermissionManager: CameraPermissionManager
): CameraRepository{
    override fun hasCameraPermission(): Boolean =
        cameraPermissionManager.hasCameraPermission()

    override fun hasStoragePermission(): Boolean =
        cameraPermissionManager.hasStoragePermission()

    override fun hasAllPermissions(): Boolean =
        cameraPermissionManager.hasAllPermissions()

    override fun getMissingPermissions(): List<String> =
        cameraPermissionManager.getMissingPermissions()

    override fun isCameraAvailable(): Boolean =
        cameraPermissionManager.isCameraAvailable()

    override suspend fun capturePhoto(): Result<String> {
        return if (hasCameraPermission()) {
            cameraService.capturePhoto()
                .map { uri -> uri.toString() }
        } else {
            Result.failure(SecurityException("Camera permission not granted"))
        }
    }

    override fun hasFlashUnit(): Boolean = cameraService.hasFlashUnit()

    override fun toggleFlash(): Result<Unit> = cameraService.toggleFlash()

    override fun releaseCamera() = cameraService.releaseCamera()
}