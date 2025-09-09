package com.example.presentation.features.main.diary.addMeals.addMealBarcode

import android.annotation.SuppressLint
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.presentation.features.main.diary.addMeals.addMealBarcode.models.AddMealBarcodeUiState
import com.example.presentation.features.main.diary.addMeals.addMealBarcode.models.BarcodeInputMode
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

    AddMealBarcodeScreen(
        modifier = modifier,
        mealType = mealType,
        date = date,
        uiState = uiState,
        baseUiState = baseUiState,
        onInitializeCamera = viewModel::initializeCamera,
        onToggleFlash = viewModel::toggleFlash,
        onSwitchInputMode = viewModel::switchInputMode,
        onBarcodeInputChange = viewModel::updateManualBarcodeInput,
        onBarcodeSubmit = {
            val barcode = viewModel.submitManualBarcode()
            barcode?.let { onNavigateToAnalyze(it) }
        },
        onNavigateBack = onBackPressed,
        onErrorConsume = viewModel::clearErrors,
        onConnectionRetry = viewModel::retryLastAction,
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
    onSwitchInputMode: (BarcodeInputMode) -> Unit,
    onBarcodeInputChange: (String) -> Unit,
    onBarcodeSubmit: () -> Unit,
    onNavigateBack: () -> Unit,
    onErrorConsume: () -> Unit,
    onConnectionRetry: () -> Unit,
) {
    Box(modifier = modifier.clip(RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))) {

        if (uiState.inputMode == BarcodeInputMode.SCANNER)
            CameraSection(
                uiState.isFlashOn,
                onInitializeCamera,
                onToggleFlash,
                onSwitchInputMode
            )
        else
            ManualBarcodeEntrySection(
                onSwitchInputMode = onSwitchInputMode,
                uiState = uiState,
                onBarcodeSubmit = onBarcodeSubmit,
                onBarcodeInputChange = onBarcodeInputChange
            )

        LoadingBackground(baseUiState.isLoading)
        HandleError(
            baseUiState = baseUiState,
            onErrorConsume = onErrorConsume,
            onConnectionRetry = onConnectionRetry
        )
    }
}


@Composable
fun ManualBarcodeEntrySection(
    onSwitchInputMode: (BarcodeInputMode) -> Unit,
    uiState: AddMealBarcodeUiState,
    onBarcodeInputChange: (String) -> Unit,
    onBarcodeSubmit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground.copy(0.02f))
    ) {
        IconButton(
            onClick = { onSwitchInputMode(BarcodeInputMode.SCANNER) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.barcode),
                contentDescription = "Switch to scanner",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 104.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Enter barcode",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(R.drawable.barcode_img),
                contentDescription = null,
                modifier = Modifier.width(200.dp),
                contentScale = ContentScale.FillWidth
            )

            Spacer(modifier = Modifier.height(16.dp))

            BarcodeInputDisplay(
                input = uiState.manualBarcodeInput,
                maxLength = 13
            )

            BarcodeTextField(
                value = uiState.manualBarcodeInput,
                onValueChange = onBarcodeInputChange,
                onDone = {
                    if (uiState.manualBarcodeInput.length >= 8) {
                        onBarcodeSubmit()
                    }
                }
            )
        }
    }
}

@Composable
private fun BarcodeInputDisplay(
    input: String,
    maxLength: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxLength) { index ->
            Box(
                modifier = Modifier
                    .size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (index < input.length) {
                    Text(
                        text = input[index].toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .width(2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun BarcodeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    TextField(
        value = value,
        onValueChange = { newValue ->
            val filteredValue = newValue.filter {
                it.isDigit() || it in listOf('-', '+', '*')
            }.take(13)
            onValueChange(filteredValue)
        },
        modifier = Modifier
            .size(0.dp)
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone() }
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun CameraSection(
    isFlashOn: Boolean,
    onInitializeCamera: (LifecycleOwner, PreviewView) -> Unit,
    onToggleFlash: () -> Unit,
    onSwitchInputMode: (BarcodeInputMode) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }

    Box {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize(),
            update = { onInitializeCamera(lifecycleOwner, it) }
        )

        CameraOverlayBox()

        IconButton(
            onClick = { onSwitchInputMode(BarcodeInputMode.MANUAL) },
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
                    if (isFlashOn) R.drawable.lightning_slash_on
                    else R.drawable.lightning_slash
                ),
                contentDescription = "Toggle flash",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
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

