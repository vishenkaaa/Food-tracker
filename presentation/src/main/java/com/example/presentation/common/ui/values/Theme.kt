package com.example.presentation.common.ui.values

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Light Theme Colors
private val LightColorScheme
    @Composable
    get() = lightColorScheme(
        primary = Green, // акцентний
        onPrimary = White, // на акцентному
        primaryContainer = DisableLightContainer, // недоступний контейнер
        secondary = Dark30, // світло сірий текст
        onSecondary = Dark60, // темніший сірий текст
        secondaryContainer = LightBackground,
        surface = Green30, // не заповнений прогрес
        onSurface = Dark50, // не вибраний текст
        surfaceVariant = Shadow, // тінь
        surfaceContainer = White, // фон контейнера
        background = LightBackground, //фон
        onBackground = Dark, // текст на фоні
        outline = Gray.copy(0.16f), // не активний контур
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
        surfaceVariant = Shadow.copy(0.08f),
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