package com.example.presentation.features.auth.google

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.Text
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.HandleError

@Composable
fun AuthRoute(
    viewModel: AuthVM = hiltViewModel()
) {
    val context = LocalContext.current
    val baseUiState by viewModel.baseUiState.collectAsState()

    AuthScreen(
        baseUiState = baseUiState,
        onLoginClicked = { viewModel.signInWithGoogle(context, forceNewAccount = true) },
        onErrorConsume = { viewModel.consumeError() }
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 72.dp, horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Food",
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.displayMedium
                )
                Text(
                    text = "Traker",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.displayMedium
                )
            }

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
            )
            Button(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth(),
                onClick = onLoginClicked,
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = !baseUiState.isLoading
            ) {
                Icon(
                    painter = painterResource(R.drawable.google),
                    tint = Color.Unspecified,
                    contentDescription = "Google",
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                )
                Text(
                    text = stringResource(R.string.login_with_google),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        if (baseUiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.6f))
                    .zIndex(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        HandleError(
            baseUiState = baseUiState,
            onErrorConsume = onErrorConsume,
            onConnectionRetry = onLoginClicked
        )
    }
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