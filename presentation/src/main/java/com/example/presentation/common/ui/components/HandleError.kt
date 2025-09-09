package com.example.presentation.common.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.values.FoodTrackTheme

@Composable
fun HandleError(
    modifier: Modifier = Modifier,
    baseUiState: BaseUiState,
    onErrorConsume: (() -> Unit)? = null,
    onConnectionRetry: (() -> Unit)? = null
) {
    when {
        baseUiState.isConnectionError -> {
            Box(
                modifier = modifier.fillMaxSize().zIndex(10f),
                contentAlignment = Alignment.BottomCenter,
            ) {
                ConnectionErrorSnackBar(
                    onErrorDismissed = { onErrorConsume?.invoke() },
                    onErrorConsumed = { onConnectionRetry?.invoke() }
                )
            }
        }

        baseUiState.unexpectedError?.isNotBlank() == true -> {
            Box(
                modifier = modifier.fillMaxSize().zIndex(10f),
                contentAlignment = Alignment.BottomCenter
            ) {
                ErrorSnackBar(
                    error = baseUiState.unexpectedError.toString(),
                    actionLabel = if (onConnectionRetry != null) stringResource(R.string.retry) else null,
                    onErrorConsumed = onConnectionRetry,
                    onErrorDismissed = onErrorConsume
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HandleErrorPreview() {
    FoodTrackTheme {
        HandleError(
            baseUiState = BaseUiState()
        )
    }
}