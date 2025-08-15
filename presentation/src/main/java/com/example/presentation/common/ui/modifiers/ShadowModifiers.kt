package com.example.presentation.common.ui.modifiers

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.softShadow(
    color: Color = MaterialTheme.colorScheme.onBackground,
    blurRadius: Dp = 8.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    spread: Dp = 0.dp,
    cornerRadius: Dp = 20.dp
): Modifier {
    val density = LocalDensity.current

    return drawBehind {
        val shadowColor = color.toArgb()

        drawIntoCanvas { canvas ->
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()

            frameworkPaint.color = android.graphics.Color.TRANSPARENT

            frameworkPaint.setShadowLayer(
                with(density) { blurRadius.toPx() },
                with(density) { offsetX.toPx() },
                with(density) { offsetY.toPx() },
                shadowColor
            )

            // Малюємо тільки тінь (об'єкт буде прозорим)
            canvas.drawRoundRect(
                left = with(density) { spread.toPx() },
                top = with(density) { spread.toPx() },
                right = size.width - with(density) { spread.toPx() },
                bottom = size.height - with(density) { spread.toPx() },
                radiusX = with(density) { cornerRadius.toPx() },
                radiusY = with(density) { cornerRadius.toPx() },
                paint = paint
            )
        }
    }
}