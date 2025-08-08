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
import com.example.presentation.R

@Composable
fun ErrorSnackbar(
    error: String,
    actionLabel: String? = null,
    onErrorDismissed: (() -> Unit)? = null,
    onErrorConsumed: (() -> Unit)? = null,
    duration: SnackbarDuration = SnackbarDuration.Short,
) {
    val snackbarHostState = remember(error) { SnackbarHostState() }

    LaunchedEffect(snackbarHostState) {
        val action = snackbarHostState.showSnackbar(
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
        hostState = snackbarHostState,
    ) {
        Snackbar(
            snackbarData = it,
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background,
            dismissActionContentColor = MaterialTheme.colorScheme.onBackground,
            actionColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(bottom = 40.dp)
        )
    }
}

@Composable
fun ConnectionErrorSnackBar(
    onErrorDismissed: (() -> Unit),
    onErrorConsumed: (() -> Unit)
) {
    ErrorSnackbar(
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
    ErrorSnackbar(
        error = "Error descr",
        actionLabel = "Action",
        onErrorConsumed = {

        }
    )
}
