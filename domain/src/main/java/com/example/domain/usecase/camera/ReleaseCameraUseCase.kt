package com.example.domain.usecase.camera

import com.example.domain.repository.CameraRepository
import javax.inject.Inject

class ReleaseCameraUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) {
    operator fun invoke() {
        cameraRepository.releaseCamera()
    }
}