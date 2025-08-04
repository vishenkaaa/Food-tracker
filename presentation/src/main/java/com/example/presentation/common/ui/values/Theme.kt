package com.example.presentation.common.ui.values

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
        primary = Green, // акцентний
        onPrimary = White, // на акцентному
        secondary = DarkGreen30, // світло сірий текст
        onSecondary = DarkGreen60, // темніший сірий текст
        surface = Green30, // не заповнений прогрес
        onSurface = DarkGreen50, // не вибраний текст
        surfaceVariant = White.copy(0.16f), // тінь
        background = White, //фон
        onBackground = DarkGreen, // текст на фоні
        outline = DarkBackground.copy(0.20f), // не активний контур
        outlineVariant = Green30, // активний контур
        error = Red, // попередження/помилка
        onError = White // на попередженні
    )

// Dark Theme Colors
private val DarkColorScheme
    @Composable
    get() = darkColorScheme(
        primary = Green,
        onPrimary = White,
        secondary = DarkGreen30,
        onSecondary = DarkGreen60,
        surface = Green30,
        onSurface = White20,
        surfaceVariant = DarkBackground.copy(0.16f),
        background = DarkBackground,
        onBackground = White,
        outline = White20,
        outlineVariant = Green30,
        error = Red,
        onError = White
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