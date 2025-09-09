package com.example.presentation.features.main.diary.addMeals.addMealBarcode

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.camera.ToggleFlashUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.camera.CameraService
import com.example.presentation.features.main.diary.addMeals.addMealBarcode.models.AddMealBarcodeUiState
import com.example.presentation.features.main.diary.addMeals.addMealBarcode.models.BarcodeInputMode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMealBarcodeVM @Inject constructor(
    private val cameraService: CameraService,
    private val toggleFlashUseCase: ToggleFlashUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(AddMealBarcodeUiState())
    val uiState: StateFlow<AddMealBarcodeUiState> = _uiState.asStateFlow()

    private var cameraProvider: ProcessCameraProvider? = null

    fun initializeCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        viewModelScope.launch {
            handleLoading(true)

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
                    handleError(
                        Exception(context.getString(R.string.error_camera_init_failed)),
                        context
                    ) {
                        initializeCamera(lifecycleOwner, previewView)
                    }
                }
                .also { handleLoading(false) }
        }
    }

    fun switchInputMode(mode: BarcodeInputMode) {
        _uiState.update { currentState ->
            currentState.copy(
                inputMode = mode,
                showManualEntry = mode == BarcodeInputMode.MANUAL
            )
        }
    }

    fun updateManualBarcodeInput(input: String) {
        _uiState.update { currentState ->
            currentState.copy(manualBarcodeInput = input)
        }
    }

    fun submitManualBarcode(): String? {
        val barcode = _uiState.value.manualBarcodeInput
        return if (barcode.length >= 8) {
            barcode
        } else {
            handleError(Exception("Barcode must be at least 8 digits long"))
            null
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
        cameraProvider?.unbindAll()
    }
}