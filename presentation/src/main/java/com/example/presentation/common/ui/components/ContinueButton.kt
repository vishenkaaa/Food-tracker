package com.example.presentation.common.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
    onNextStep: () -> Unit
) {
    Button(
        onClick = { onNextStep() },
        modifier = Modifier
            .padding(horizontal = 45.dp)
            .fillMaxWidth()
            .padding(bottom = 72.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            stringResource(R.string.continue_),
            Modifier.padding(vertical = 10.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}