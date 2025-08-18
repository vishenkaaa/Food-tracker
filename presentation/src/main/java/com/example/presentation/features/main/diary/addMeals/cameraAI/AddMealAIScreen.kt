package com.example.presentation.features.main.diary.addMeals.cameraAI

import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.diary.MealType
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.LeftAlignedHeader
import com.example.presentation.extensions.displayName
import com.example.presentation.features.main.diary.addMeals.cameraAI.models.CameraAIUiState
import java.time.LocalDate

@Composable
fun AddMealAIRoute(
    mealType: MealType,
    date: LocalDate,
    viewModel: AddMealAIVM = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()

    AddMealAIScreen(
        mealType = mealType,
        date = date,
        uiState = uiState,
        baseUiState = baseUiState,
        onInitializeCamera = viewModel::initializeCamera,
        onCapturePhoto = viewModel::capturePhoto,
        onToggleFlash = viewModel::toggleFlash,
        onNavigateBack = { },
        onErrorConsume = viewModel::clearErrors,
        onRequestGalleryPermissions = {  }
    )
}

@Composable
fun AddMealAIScreen(
    mealType: MealType,
    date: LocalDate,
    uiState: CameraAIUiState,
    baseUiState: BaseUiState,
    onInitializeCamera: (LifecycleOwner, PreviewView) -> Unit,
    onCapturePhoto: () -> Unit,
    onToggleFlash: () -> Unit,
    onNavigateBack: () -> Unit,
    onErrorConsume: () -> Unit,
    onRequestGalleryPermissions: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val content = LocalContext.current
    val previewView = remember { PreviewView(content) }

    Box{
        Scaffold(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            topBar = {
                LeftAlignedHeader(
                    mealType = mealType,
                    onNavigateBack = onNavigateBack
                )
            },
        ){ padding ->
            Box(
                Modifier.padding(padding)
            )
        }

    }
}