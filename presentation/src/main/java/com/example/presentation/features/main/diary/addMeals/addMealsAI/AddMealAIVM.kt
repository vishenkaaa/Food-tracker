package com.example.presentation.features.main.diary.addMeals.addMealsAI

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.camera.CapturePhotoUseCase
import com.example.domain.usecase.camera.CheckCameraPermissionUseCase
import com.example.domain.usecase.camera.ReleaseCameraUseCase
import com.example.domain.usecase.camera.ToggleFlashUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.camera.CameraService
import com.example.presentation.features.main.diary.addMeals.addMealsAI.models.AddMealAIUiState
import com.example.presentation.features.main.diary.addMeals.addMealsAI.models.StoragePermissionState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMealAIVM @Inject constructor(
    private val checkCameraPermissionUseCase: CheckCameraPermissionUseCase,
    private val capturePhotoUseCase: CapturePhotoUseCase,
    private val toggleFlashUseCase: ToggleFlashUseCase,
    private val releaseCameraUseCase: ReleaseCameraUseCase,
    private val cameraService: CameraService,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AddMealAIUiState())
    val uiState: StateFlow<AddMealAIUiState> = _uiState.asStateFlow()

    private val _storagePermissionState = MutableStateFlow(StoragePermissionState())
    val storagePermissionState: StateFlow<StoragePermissionState> = _storagePermissionState.asStateFlow()

    fun handleStorageAccess() {
        checkStoragePermissions()
        val currentState = _storagePermissionState.value

        when {
            currentState.hasPermission -> {
                _storagePermissionState.update {
                    it.copy(shouldOpenGallery = true)
                }
            }
            currentState.permanentlyDenied -> {
                handleError(Exception(context.getString(R.string.error_storage_permission_required)))
            }
            else -> requestStoragePermissions()
        }
    }

    fun onPhotoSelectedFromGallery(uri: String) {
        _uiState.update {
            it.copy(
                capturedPhotoUri = uri,
            )
        }
    }

    fun resetGalleryOpenRequest() {
        _storagePermissionState.update {
            it.copy(shouldOpenGallery = false)
        }
    }

    private fun checkStoragePermissions(){
        val granted = checkCameraPermissionUseCase.hasStoragePermission()

        _storagePermissionState.update {
            it.copy(
                hasPermission = granted,
                permanentlyDenied = if (granted) false else it.permanentlyDenied
            )
        }
    }

    private fun requestStoragePermissions() {
        _storagePermissionState.update {
            it.copy(
                shouldRequest = true,
            )
        }
    }

    fun onStoragePermissionResult(granted: Boolean, permanentlyDenied: Boolean) {
        _storagePermissionState.update {
            it.copy(
                shouldRequest = false,
                hasPermission = granted,
                permanentlyDenied = permanentlyDenied
            )
        }
    }

    fun resetStoragePermissionRequest() {
        _storagePermissionState.update { it.copy(shouldRequest = false) }
    }

    fun initializeCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        viewModelScope.launch {
            handleLoading(true)

            if (!checkCameraPermissionUseCase.hasCameraPermission()) {
                handleError(Exception(context.getString(R.string.error_camera_permission_required)))
                return@launch
            }

            cameraService.initializeCamera(lifecycleOwner, previewView)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isCameraReady = true,
                            hasFlashUnit = toggleFlashUseCase.hasFlashUnit()
                        )
                    }
                } 
                .onFailure {
                    handleError(Exception(context.getString(R.string.error_camera_init_failed)), context) {
                        initializeCamera(lifecycleOwner, previewView)
                    }
                }
                .also { handleLoading(false) }
        }
    }

    fun capturePhoto() { 
        if (!_uiState.value.isCameraReady) {
            handleError(Exception(context.getString(R.string.error_camera_not_ready)))
            return
        }

        viewModelScope.launch {
            handleLoading(true)

            capturePhotoUseCase()
                .onSuccess { filePath ->
                    _uiState.update { it.copy(capturedPhotoUri = filePath) }
                }
                .onFailure {
                    handleError(Exception(context.getString(R.string.error_capture_failed)))
                }
                .also { handleLoading(false) }
        }
    }

    fun toggleFlash() {
        if (!_uiState.value.hasFlashUnit){
            handleError(Exception(context.getString(R.string.error_flash_not_available)))
            return
        }

        toggleFlashUseCase()
            .onSuccess {
                _uiState.update { it.copy(isFlashOn = !it.isFlashOn) }
            }
            .onFailure {
                handleError(Exception(context.getString(R.string.error_flash_toggle_failed)))
            }
    }

    override fun onCleared() {
        super.onCleared()
        releaseCameraUseCase()
    }
}