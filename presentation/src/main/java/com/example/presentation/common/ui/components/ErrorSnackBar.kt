package com.example.presentation.common.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    ) { snackbarData ->
        Snackbar(
            modifier = Modifier
                .padding(bottom = 60.dp)
                .padding(horizontal = 16.dp)
                .zIndex(1f),
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.background,
            action = if (snackbarData.visuals.actionLabel != null) {
                {
                    TextButton(
                        onClick = { snackbarData.performAction() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = snackbarData.visuals.actionLabel!!,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else null,
            dismissAction = if (snackbarData.visuals.withDismissAction) {
                {
                    IconButton(
                        onClick = { snackbarData.dismiss() }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            } else null
        ) {
            Text(
                text = snackbarData.visuals.message,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = if (actionLabel != null) 8.dp else 0.dp)
            )
        }
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
