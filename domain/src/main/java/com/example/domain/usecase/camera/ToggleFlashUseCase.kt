package com.example.domain.usecase.camera

import com.example.domain.repository.CameraRepository
import javax.inject.Inject

class ToggleFlashUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) {
    operator fun invoke(): Result<Unit> {
        return cameraRepository.toggleFlash()
    }

    fun hasFlashUnit(): Boolean = cameraRepository.hasFlashUnit()
}