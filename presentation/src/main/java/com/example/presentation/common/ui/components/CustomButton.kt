package com.example.presentation.common.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.presentation.R

@Composable
fun CustomButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    iconPositionStart: Boolean = true,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(icon!=null && iconPositionStart)
                Icon(
                    painter = icon,
                    tint = Color.Unspecified,
                    contentDescription = text,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            if(icon!=null && !iconPositionStart)
                Icon(
                    painter = icon,
                    tint = Color.Unspecified,
                    contentDescription = text,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CustomButtonPreview(){
    Column {
        CustomButton(
            "Text", Modifier, painterResource(R.drawable.google)
        ) {}
        CustomButton(
            "Text"
        ) {}
    }
}