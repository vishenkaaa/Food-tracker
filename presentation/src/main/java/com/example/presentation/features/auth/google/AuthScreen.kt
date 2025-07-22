package com.example.presentation.features.auth.google

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.Text
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.HandleError

@Composable
fun AuthRoute(
    viewModel: AuthVM = hiltViewModel()
) {
    val context = LocalContext.current
    val baseUiState by viewModel.baseUiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AuthScreen(
            baseUiState = baseUiState,
            onLoginClicked = { viewModel.signInWithGoogle(context, forceNewAccount = true) },
            onErrorConsume = { viewModel.consumeError() }
        )
    }
}

@Composable
fun AuthScreen(
    baseUiState: BaseUiState,
    onLoginClicked: () -> Unit,
    onErrorConsume: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome!", color = Color.Black)
        Button(
            modifier = Modifier.padding(top = 32.dp),
            onClick = onLoginClicked,
            enabled = !baseUiState.isLoading
        ) {
            Text("Login with Google")
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

@Preview
@Composable
private fun AuthScreenPreview(){
    AuthScreen(
        baseUiState = BaseUiState(),
        onErrorConsume = {},
        onLoginClicked = {}
    )
}