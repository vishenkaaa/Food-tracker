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
        surfaceVariant = Color.Black.copy(0.20f), // тінь
        surfaceContainer = White, // фон контейнера
        background = LightBackground, //фон
        onBackground = DarkGreen, // текст на фоні
        outline = Dark.copy(0.16f), // не активний контур
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
        secondary = White.copy(0.8f),
        onSecondary = White.copy(0.6f),
        surface = Green30,
        onSurface = White.copy(0.4f),
        surfaceVariant = Color.Black.copy(0.20f),
        surfaceContainer = DarkContainer,
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