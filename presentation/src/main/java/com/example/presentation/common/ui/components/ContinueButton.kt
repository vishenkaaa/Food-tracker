package com.example.presentation.common.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.presentation.R

@Composable
fun ContinueButton(
    enabled: Boolean = false,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    onNextStep: () -> Unit
) {
    Button(
        onClick = { onNextStep() },
        modifier = modifier
            .padding(horizontal = 45.dp)
            .fillMaxWidth()
            .padding(bottom = 72.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.outline
        )
    ) {
        Text(
            stringResource(R.string.continue_),
            Modifier.padding(vertical = 10.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}