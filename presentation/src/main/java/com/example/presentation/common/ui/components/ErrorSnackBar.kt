package com.example.presentation.common.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.presentation.R
import com.example.presentation.common.ui.values.FoodTrackTheme

@Composable
fun ErrorSnackBar(
    error: String,
    actionLabel: String? = null,
    onErrorDismissed: (() -> Unit)? = null,
    onErrorConsumed: (() -> Unit)? = null,
    duration: SnackbarDuration = SnackbarDuration.Long,
) {
    val snackBarHostState = remember(error) { SnackbarHostState() }

    LaunchedEffect(snackBarHostState) {
        val action = snackBarHostState.showSnackbar(
            message = error,
            actionLabel = actionLabel,
            duration = duration,
            withDismissAction = actionLabel != null
        )
        when (action) {
            SnackbarResult.Dismissed -> {
                onErrorDismissed?.invoke()
            }
            SnackbarResult.ActionPerformed -> {
                onErrorConsumed?.invoke()
            }
        }
    }

    SnackbarHost(
        hostState = snackBarHostState,
    ) {
        Snackbar(
            snackbarData = it,
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background,
            dismissActionContentColor = MaterialTheme.colorScheme.onBackground,
            actionColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(bottom = 40.dp).zIndex(1f)
        )
    }
}

@Composable
fun ConnectionErrorSnackBar(
    onErrorDismissed: (() -> Unit),
    onErrorConsumed: (() -> Unit)
) {
    ErrorSnackBar(
        error = stringResource(R.string.no_internet_connection),
        actionLabel = stringResource(R.string.retry),
        duration = SnackbarDuration.Indefinite,
        onErrorDismissed = {
            onErrorDismissed.invoke()
        },
        onErrorConsumed = {
            onErrorConsumed.invoke()
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun ConnectionErrorSnackBarPreview() {
    FoodTrackTheme {
        ErrorSnackBar(
            error = "Error descr",
            actionLabel = "Action",
            onErrorConsumed = {

            }
        )
    }
}
