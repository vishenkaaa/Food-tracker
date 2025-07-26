package com.example.presentation.common.values

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light Theme Colors
private val LightColorScheme
    @Composable
    get() = lightColorScheme(
        primary = Green,
        onPrimary = White,
        secondary = Green50,
        onSecondary = DarkGreen,
        surface = White,
        onSurface = DarkGreen,
        surfaceVariant = Green,
        onSurfaceVariant = GrayDark,
        background = White,
        onBackground = DarkGreen,
        outline = Green50,
    )

// Dark Theme Colors
private val DarkColorScheme
    @Composable
    get() = darkColorScheme(
        primary = Green,
        onPrimary = White,
        secondary = GrayLight,
        onSecondary = DarkGreen,
        surface = DarkBackground,
        onSurface = White,
        surfaceVariant = Green,
        onSurfaceVariant = GrayLight,
        background = DarkBackground,
        onBackground = White,
        outline = Green50
    )

@Composable
fun FoodTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}