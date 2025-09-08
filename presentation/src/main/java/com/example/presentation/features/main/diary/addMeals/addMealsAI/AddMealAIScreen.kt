package com.example.presentation.features.main.diary.addMeals.addMealsAI

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.diary.MealType
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.camera.CameraPermissionManager
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.LoadingBackground
import com.example.presentation.features.main.diary.addMeals.addMealsAI.models.AddMealAIUiState
import com.example.presentation.features.main.diary.extensions.findActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import java.time.LocalDate

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddMealAIRoute(
    modifier: Modifier = Modifier,
    mealType: MealType,
    date: LocalDate,
    onBackPressed: () -> Unit,
    onNavigateToAnalyze: (String) -> Unit,
    viewModel: AddMealAIVM = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()
    val storagePermissionState by viewModel.storagePermissionState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if(uri!=null) viewModel.onPhotoSelectedFromGallery(uri.toString())
    }

    val galleryPermissionLauncher = rememberPermissionState(
        permission = CameraPermissionManager.REQUIRED_STORAGE_PERMISSION
    ) { granted ->
        val permanentlyDenied = !granted && !ActivityCompat.shouldShowRequestPermissionRationale(
            context.findActivity(),
            CameraPermissionManager.REQUIRED_STORAGE_PERMISSION
        )

        viewModel.onStoragePermissionResult(granted, permanentlyDenied)

        if (granted) {
            galleryLauncher.launch("image/*")
        }
    }

    LaunchedEffect(storagePermissionState.shouldRequest) {
        if (storagePermissionState.shouldRequest) {
            galleryPermissionLauncher.launchPermissionRequest()
            viewModel.resetStoragePermissionRequest()
        }
    }

    LaunchedEffect(storagePermissionState.shouldOpenGallery) {
        if (storagePermissionState.shouldOpenGallery) {
            galleryLauncher.launch("image/*")
            viewModel.resetGalleryOpenRequest()
        }
    }

    LaunchedEffect(uiState.capturedPhotoUri) {
        if(uiState.capturedPhotoUri!=null)
            onNavigateToAnalyze(uiState.capturedPhotoUri!!)
    }

    AddMealAIScreen(
        modifier = modifier,
        mealType = mealType,
        date = date,
        uiState = uiState,
        baseUiState = baseUiState,
        onInitializeCamera = viewModel::initializeCamera,
        onCapturePhoto = viewModel::capturePhoto,
        onToggleFlash = viewModel::toggleFlash,
        onNavigateBack = { onBackPressed() },
        onErrorConsume = viewModel::clearErrors,
        onGalleryClick = { viewModel.handleStorageAccess() },
        onConnectionRetry = { viewModel.retryLastAction() }
    )
}

@Composable
fun AddMealAIScreen(
    modifier: Modifier = Modifier,
    mealType: MealType,
    date: LocalDate,
    uiState: AddMealAIUiState,
    baseUiState: BaseUiState,
    onInitializeCamera: (LifecycleOwner, PreviewView) -> Unit,
    onCapturePhoto: () -> Unit,
    onToggleFlash: () -> Unit,
    onNavigateBack: () -> Unit,
    onErrorConsume: () -> Unit,
    onConnectionRetry: () -> Unit,
    onGalleryClick: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val content = LocalContext.current
    val previewView = remember { PreviewView(content) }

    LaunchedEffect(Unit) {
        onInitializeCamera(lifecycleOwner, previewView)
    }

    Box(modifier = modifier){
        Box(
            modifier = Modifier.clip(RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
        ){
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize(),
                update = { onInitializeCamera(lifecycleOwner, it) }
            )
        }

        CameraOverlayBox()

        CameraControlsBar(
            uiState = uiState,
            baseUiState = baseUiState,
            onGalleryClick = onGalleryClick,
            onCapturePhoto = onCapturePhoto,
            onToggleFlash = onToggleFlash,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        LoadingBackground(baseUiState.isLoading)
        HandleError(
            baseUiState = baseUiState,
            onErrorConsume = onErrorConsume,
            onConnectionRetry = { onConnectionRetry() }
        )
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun CameraOverlayBox() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current
    val boxSize = screenWidth * 0.8f

    Canvas(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val rectSize = with(density) { boxSize.toPx() }
        val left = (canvasWidth - rectSize) / 2f
        val top = (canvasHeight - rectSize) / 2f

        drawRect(
            color = Color.Black.copy(alpha = 0.6f),
            size = size
        )

        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(rectSize, rectSize),
            cornerRadius = CornerRadius(20.dp.toPx()),
            blendMode = BlendMode.Clear
        )

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(left, top),
            size = Size(rectSize, rectSize),
            cornerRadius = CornerRadius(20.dp.toPx()),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

@Composable
fun CameraControlsBar(
    uiState: AddMealAIUiState,
    baseUiState: BaseUiState,
    onGalleryClick: () -> Unit,
    onCapturePhoto: () -> Unit,
    onToggleFlash: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp, vertical = 34.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onGalleryClick() },
            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.gallery),
                contentDescription = "Gallery",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onCapturePhoto() },
            contentAlignment = Alignment.Center
        ){
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }

        IconButton(
            onClick = onToggleFlash,
            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                painter = if (uiState.isFlashOn) painterResource(R.drawable.lightning_slash_on)
                else painterResource(R.drawable.lightning_slash),
                contentDescription = "Flash",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
