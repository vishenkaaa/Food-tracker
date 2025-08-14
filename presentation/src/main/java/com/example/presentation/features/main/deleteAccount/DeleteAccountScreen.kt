package com.example.presentation.features.main.deleteAccount

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.CustomButton
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.LoadingBackground

@Composable
fun DeleteAccountRoute(
    viewModel: DeleteAccountVM = hiltViewModel(),
    onCancelDelete: () -> Unit
) {
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val successDeleteMessage = stringResource(R.string.success_delete)
    LaunchedEffect(Unit) {
        viewModel.showToast.collect {
            Toast.makeText(context, successDeleteMessage, Toast.LENGTH_SHORT).show()
        }
    }

    DeleteAccountScreen(
        baseUiState = baseUiState,
        onErrorConsume = { viewModel.consumeError() },
        onDelete = { viewModel.onDelete() },
        onCancel = { onCancelDelete() }
    )
}

@Composable
fun DeleteAccountScreen(
    baseUiState: BaseUiState,
    onErrorConsume: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.weight(1f))
            WarningContent()

            Spacer(modifier = Modifier.weight(1f))
            ActionsSection(
                isLoading = baseUiState.isLoading,
                onDelete = onDelete,
                onCancel = onCancel
            )
        }

        LoadingBackground(baseUiState.isLoading)

        HandleError(
            baseUiState = baseUiState,
            onErrorConsume = { onErrorConsume() },
            onConnectionRetry = { onDelete() }
        )
    }
}

@Composable
private fun ActionsSection(
    isLoading: Boolean,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        CustomButton(
            text = stringResource(R.string.delete_account),
            color = MaterialTheme.colorScheme.error,
            onClick = onDelete
        )

        TextButton(
            onClick = onCancel,
            enabled = !isLoading,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(R.string.cancel),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun WarningContent() {
    Column(
        modifier = Modifier.padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.warning_circle),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .size(32.dp)
        )
        Text(
            text = stringResource(R.string.attention),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.delete_attention),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun DeleteAccountScreenPreview (){
    DeleteAccountScreen(
        baseUiState = BaseUiState(),
        onDelete = {},
        onCancel = {},
        onErrorConsume = {}
    )
}