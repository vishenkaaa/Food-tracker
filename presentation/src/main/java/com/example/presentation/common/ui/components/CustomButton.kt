package com.example.presentation.common.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    text: String,
    icon: Painter? = null,
    iconPositionStart: Boolean = true,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    Button(
        modifier = Modifier
            .padding(top = 32.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled
    ) {
        if(icon!=null && iconPositionStart)
            Icon(
                painter = icon,
                tint = Color.Unspecified,
                contentDescription = text,
                modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
            )
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        if(icon!=null && !iconPositionStart)
            Icon(
                painter = icon,
                tint = Color.Unspecified,
                contentDescription = text,
                modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
            )
    }
}