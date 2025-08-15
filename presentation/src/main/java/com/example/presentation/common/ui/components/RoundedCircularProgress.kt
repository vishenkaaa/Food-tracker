package com.example.presentation.common.ui.components
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundedCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 8.dp,
    progressColor: Color = Color.Blue,
    trackColor: Color = Color.Gray
) {
    Canvas(modifier = modifier) {
        val strokeWidthPx = strokeWidth.toPx()
        val center = Offset(size.width / 2, size.height / 2)
        val radius = (size.minDimension - strokeWidthPx) / 2

        drawCircle(
            color = trackColor,
            radius = radius,
            center = center,
            style = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Round
            )
        )

        val sweepAngle = 360f * progress
        if (sweepAngle > 0) {
            drawArc(
                color = progressColor,
                startAngle = -88f,
                sweepAngle = -sweepAngle,
                useCenter = false,
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(
                    width = strokeWidthPx,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}