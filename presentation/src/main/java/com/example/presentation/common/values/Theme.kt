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
        primary = MainAccentColor,
        onPrimary = White,
        secondary = Gray,
        onSecondary = White,
        surface = White10,
        onSurface = DarkColor,
        onSurfaceVariant = GrayDark,
        background = White,
        onBackground = DarkColor,
        outline = GrayLight,
        surfaceVariant = White50
    )

// Dark Theme Colors
private val DarkColorScheme
    @Composable
    get() = darkColorScheme(
        primary = MainAccentColor,
        onPrimary = White,
        secondary = GrayLight,
        onSecondary = DarkColor,
        surface = DarkColor,
        onSurface = White,
        onSurfaceVariant = GrayLight,
        background = DarkColor,
        onBackground = White,
        outline = Gray,
        surfaceVariant = Gray
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