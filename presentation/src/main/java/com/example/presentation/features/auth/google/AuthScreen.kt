package com.example.presentation.features.auth.google

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
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

@Composable
fun AuthRoute(
    viewModel: AuthVM = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as Activity
    val baseUiState by viewModel.baseUiState.collectAsState()

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleOldGoogleSignInResult(result, context)
    }

    LaunchedEffect(viewModel) {
        viewModel.googleSignInIntent.collect { intent ->
            googleSignInLauncher.launch(intent)
        }
    }

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
                .padding(vertical = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimary)) {
                        append(stringResource(R.string.food))
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(stringResource(R.string.snap))
                    }
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimary)) {
                        append(stringResource(R.string.your_daily_guide_to))
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(" ${stringResource(R.string.smart)}")
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimary)) {
                        append(stringResource(R.string.eating))
                    }
                },
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .padding(horizontal = 32.dp)
            )
            CustomButton(
                text = stringResource(R.string.login_with_google),
                icon = painterResource(R.drawable.google),
                onClick = { onLoginClicked() },
                iconPositionStart = true
            )
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
fun AuthBackground(){
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
                Color(0xFF0C2004).copy(0.4f),
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
                                Color(0xFF0C2004).copy(0.8f),
                                Color(0xFF0C2004).copy(0.8f),
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
                                Color(0xFF0C2004).copy(0.6f),
                                Color(0xFF0C2004).copy(0.5f),
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
    AuthScreen(
        baseUiState = BaseUiState(),
        onErrorConsume = {},
        onLoginClicked = {}
    )
}