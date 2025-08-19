package com.example.presentation.common.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.presentation.arch.BaseUiState

@Composable
fun HandleError(
    modifier: Modifier = Modifier,
    baseUiState: BaseUiState,
    onErrorConsume: (() -> Unit)? = null,
    onConnectionRetry: (() -> Unit)? = null
) {

    when {
        baseUiState.isConnectionError || onConnectionRetry!=null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                ConnectionErrorSnackBar(
                    onErrorDismissed = { onErrorConsume?.invoke() },
                    onErrorConsumed = { onConnectionRetry?.invoke() }
                )
            }
        }

        baseUiState.unexpectedError?.isNotBlank() == true -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                ErrorSnackbar(error = baseUiState.unexpectedError.toString())
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HandleErrorPreview() {
    HandleError(
        baseUiState = BaseUiState()
    )
}