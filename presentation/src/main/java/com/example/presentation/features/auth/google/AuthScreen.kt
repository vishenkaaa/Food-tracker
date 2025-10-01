package com.example.presentation.features.auth.google

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.CustomButton
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.LoadingBackground
import com.example.presentation.common.ui.values.Dark
import com.example.presentation.common.ui.values.FoodTrackTheme

@Composable
fun AuthRoute(
    viewModel: AuthVM = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as Activity
    val baseUiState by viewModel.baseUiState.collectAsState()

    AuthScreen(
        baseUiState = baseUiState,
        onLoginClicked = { viewModel.signInWithGoogle(activity, forceNewAccount = true) },
        onErrorConsume = { viewModel.clearErrors() }
    )
}

@Composable
fun AuthScreen(
    baseUiState: BaseUiState,
    onLoginClicked: () -> Unit,
    onErrorConsume: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AuthBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppTitle()
            Spacer(Modifier.weight(1f))
            AppDescription()

            CustomButton(
                text = stringResource(R.string.login_with_google),
                icon = painterResource(R.drawable.google),
                onClick = { onLoginClicked() },
                iconPositionStart = true
            )

            TermsAndPrivacyText()
        }

        LoadingBackground(baseUiState.isLoading)

        HandleError(
            baseUiState = baseUiState,
            onErrorConsume = onErrorConsume,
            onConnectionRetry = onLoginClicked
        )
    }
}

@Composable
private fun AppTitle() {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onPrimary)) {
                append(stringResource(R.string.food))
            }
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append(stringResource(R.string.snap))
            }
        },
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.displayMedium,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun AppDescription() {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onPrimary)) {
                append(stringResource(R.string.your_daily_guide_to))
            }
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append(" ${stringResource(R.string.smart)}")
            }
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onPrimary)) {
                append(stringResource(R.string.eating))
            }
        },
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.displayLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 32.dp)
    )
}

@Composable
fun TermsAndPrivacyText() {
    val uriHandler = LocalUriHandler.current
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

    val annotatedText = buildAnnotatedString {
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.onPrimary.copy(0.7f))) {
            append(stringResource(R.string.continuing_you_accept_our))
        }
        pushStringAnnotation(
            tag = "TERMS",
            annotation = stringResource(R.string.terms_url)
        )
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append(stringResource(R.string.terms_of_service))
        }
        pop()

        withStyle(SpanStyle(color = MaterialTheme.colorScheme.onPrimary.copy(0.7f))) {
            append(stringResource(R.string.and))
        }
        pushStringAnnotation(
            tag = "PRIVACY",
            annotation = stringResource(R.string.privacy_url)
        )
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append(stringResource(R.string.privacy_policy))
        }
        pop()
    }

    Text(
        text = annotatedText,
        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
        onTextLayout = { layoutResult.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 16.dp)
            .navigationBarsPadding()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    layoutResult.value?.let { layout ->
                        val position = layout.getOffsetForPosition(offset)

                        annotatedText
                            .getStringAnnotations(tag = "TERMS", start = position, end = position)
                            .firstOrNull()
                            ?.let { uriHandler.openUri(it.item) }

                        annotatedText
                            .getStringAnnotations(tag = "PRIVACY", start = position, end = position)
                            .firstOrNull()
                            ?.let { uriHandler.openUri(it.item) }
                    }
                }
            }
    )
}

@Composable
fun AuthBackground(){
    val darkColor = Dark

    Image(
        painter = painterResource(id = R.drawable.login_bg),
        contentDescription = "Login background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                darkColor.copy(0.4f),
            )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                object : ShaderBrush() {
                    override fun createShader(size: Size): Shader {
                        return RadialGradientShader(
                            colors = listOf(
                                darkColor.copy(0.8f),
                                darkColor.copy(0.8f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2f, -size.height * 0.2f),
                            radius = size.width,
                            colorStops = listOf(0f, 0.5f, 1f)
                        )
                    }
                }
            )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                object : ShaderBrush() {
                    override fun createShader(size: Size): Shader {
                        return RadialGradientShader(
                            colors = listOf(
                                darkColor.copy(0.8f),
                                darkColor.copy(0.6f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2f, size.height * 1.2f),
                            radius = size.width * 1.5f,
                            colorStops = listOf(0f, 0.8f, 1f)
                        )
                    }
                }
            )
    )
}

@Preview
@Composable
private fun AuthScreenPreview() {
    FoodTrackTheme {
        AuthScreen(
            baseUiState = BaseUiState(),
            onErrorConsume = {},
            onLoginClicked = {}
        )
    }
}