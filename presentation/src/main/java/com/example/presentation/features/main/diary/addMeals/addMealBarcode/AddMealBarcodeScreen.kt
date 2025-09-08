package com.example.presentation.features.main.diary.addMeals.addMealBarcode

import android.annotation.SuppressLint
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.diary.MealType
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.LoadingBackground
import java.time.LocalDate

@Composable
fun AddMealBarcodeRoute(
    modifier: Modifier = Modifier,
    mealType: MealType,
    date: LocalDate,
    onBackPressed: () -> Unit,
    onNavigateToAnalyze: (String) -> Unit,
    viewModel: AddMealBarcodeVM = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.productFound) {
        if (uiState.productFound && uiState.scannedBarcode != null) {
            onNavigateToAnalyze(uiState.scannedBarcode!!)
        }
    }

    AddMealBarcodeScreen(
        modifier = modifier,
        mealType = mealType,
        date = date,
        uiState = uiState,
        baseUiState = baseUiState,
        onInitializeCamera = viewModel::initializeCamera,
        onToggleFlash = viewModel::toggleFlash,
        onNavigateBack = onBackPressed,
        onErrorConsume = viewModel::clearErrors,
        onConnectionRetry = viewModel::retryLastAction,
        onDismissBarcodeResult = viewModel::dismissBarcodeResult
    )
}

@Composable
fun AddMealBarcodeScreen(
    modifier: Modifier = Modifier,
    mealType: MealType,
    date: LocalDate,
    uiState: AddMealBarcodeUiState,
    baseUiState: BaseUiState,
    onInitializeCamera: (LifecycleOwner, PreviewView) -> Unit,
    onToggleFlash: () -> Unit,
    onNavigateBack: () -> Unit,
    onErrorConsume: () -> Unit,
    onConnectionRetry: () -> Unit,
    onDismissBarcodeResult: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize(),
                update = { onInitializeCamera(lifecycleOwner, it) }
            )
        }

        CameraOverlayBox()

        IconButton(
            onClick = {},
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.barcode_manualy),
                contentDescription = "Enter barcode",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = onToggleFlash,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
        ) {
            Icon(
                painter = painterResource(
                    if (uiState.isFlashOn) R.drawable.lightning_slash_on
                    else R.drawable.lightning_slash
                ),
                contentDescription = "Toggle flash",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        LoadingBackground(baseUiState.isLoading)
        HandleError(
            baseUiState = baseUiState,
            onErrorConsume = onErrorConsume,
            onConnectionRetry = onConnectionRetry
        )
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun CameraOverlayBox() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current
    val boxSize = screenWidth * 0.8f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
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

        Text(
            text = stringResource(R.string.scan_barcode_here),
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (screenWidth * 0.8f) / 2 + 16.dp)
        )
    }
}

