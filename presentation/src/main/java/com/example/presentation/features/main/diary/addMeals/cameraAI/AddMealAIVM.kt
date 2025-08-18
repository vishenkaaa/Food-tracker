package com.example.presentation.features.main.diary.addMeals.cameraAI

import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.camera.CapturePhotoUseCase
import com.example.domain.usecase.camera.CheckCameraPermissionUseCase
import com.example.domain.usecase.camera.ReleaseCameraUseCase
import com.example.domain.usecase.camera.ToggleFlashUseCase
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.camera.CameraService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri
import com.example.presentation.features.main.diary.addMeals.cameraAI.models.CameraAIUiState

@HiltViewModel
class AddMealAIVM @Inject constructor(
    private val checkCameraPermissionUseCase: CheckCameraPermissionUseCase,
    private val capturePhotoUseCase: CapturePhotoUseCase,
    private val toggleFlashUseCase: ToggleFlashUseCase,
    private val releaseCameraUseCase: ReleaseCameraUseCase,
    private val cameraService: CameraService
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CameraAIUiState())
    val uiState: StateFlow<CameraAIUiState> = _uiState.asStateFlow()

    init {
        checkPermissions()
    }

    fun initializeCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        viewModelScope.launch {
            handleLoading(true)

            if (!checkCameraPermissionUseCase.hasCameraPermission()) {
                _uiState.value = _uiState.value.copy(
                    error = "Camera permissions not granted"
                )
                return@launch
            }

            cameraService.initializeCamera(lifecycleOwner, previewView)
                .onSuccess {
                    handleLoading(false)
                    _uiState.value = _uiState.value.copy(
                        isCameraReady = true,
                        hasFlashUnit = toggleFlashUseCase.hasFlashUnit()
                    )
                }
                .onFailure { exception ->
                    handleLoading(false)
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to initialize camera"
                    )
                }
        }
    }

    fun capturePhoto() {
        viewModelScope.launch {
            handleLoading(true)
            _uiState.value = _uiState.value.copy(error = null)

            capturePhotoUseCase()
                .onSuccess { filePath ->
                    val uri = filePath.toUri()
                    handleLoading(false)
                    _uiState.value = _uiState.value.copy(
                        capturedPhotoUri = uri
                    )
                }
                .onFailure { exception ->
                    handleLoading(false)
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to capture photo"
                    )
                }
        }
    }

    fun toggleFlash() {
        toggleFlashUseCase()
            .onSuccess {
                _uiState.value = _uiState.value.copy(
                    isFlashOn = !_uiState.value.isFlashOn
                )
            }
            .onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    error = exception.message ?: "Failed to toggle flash"
                )
            }
    }

    private fun checkPermissions(){
        val hasPermissions = checkCameraPermissionUseCase.hasCameraPermission()
        val isAvailable = checkCameraPermissionUseCase.isCameraAvailable()

        _uiState.update {
            it.copy(
                hasPermissions = hasPermissions && isAvailable
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        releaseCameraUseCase()
    }
}
