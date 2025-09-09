package com.example.presentation.common.ui.values

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light Theme Colors
private val LightColorScheme
    @Composable
    get() = lightColorScheme(
        primary = Green, // акцентний
        onPrimary = White, // на акцентному
        primaryContainer = DisableLightContainer, // недоступний контейнер
        secondary = DarkGreen30, // світло сірий текст
        onSecondary = DarkGreen60, // темніший сірий текст
        secondaryContainer = LightBackground,
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
        primaryContainer = DisableDarkContainer,
        secondary = White.copy(0.8f),
        onSecondary = White.copy(0.6f),
        secondaryContainer = DarkContainer,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}