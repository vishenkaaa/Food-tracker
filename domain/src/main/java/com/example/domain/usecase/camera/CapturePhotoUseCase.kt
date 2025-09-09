package com.example.domain.usecase.camera

import com.example.domain.repository.CameraRepository
import javax.inject.Inject

class CapturePhotoUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) {
    suspend operator fun invoke(): Result<String> {
        return cameraRepository.capturePhoto()
    }
}