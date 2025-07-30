package com.example.presentation.common.values

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.presentation.R

val gilroyFontFamily = FontFamily(
    Font(R.font.gilroy_bold, FontWeight.Bold),
    Font(R.font.gilroy_medium, FontWeight.Medium),
    Font(R.font.gilroy_regular, FontWeight.Normal)
)

val display1: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 46.sp,
        lineHeight = 60.sp,
        color = if (isSystemInDarkTheme()) White else DarkGreen
    )

val display2: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 60.sp,
        color = if (isSystemInDarkTheme()) White else DarkGreen
    )

val headline1: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 36.sp,
        color = if (isSystemInDarkTheme()) White else DarkGreen
    )

val headline2: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 30.sp,
        color = if (isSystemInDarkTheme()) White else DarkGreen
    )

val subtitle1: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        color = if (isSystemInDarkTheme()) White else DarkGreen
    )

val subtitle2: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        color = if (isSystemInDarkTheme()) White else DarkGreen
    )

val body2: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        color = if (isSystemInDarkTheme()) White else DarkGreen
    )

val body1: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        color = if (isSystemInDarkTheme()) White else DarkGreen
    )

val Typography: Typography
    @Composable
    get() = Typography(
        displayLarge = display1,
        displayMedium = display2,
        headlineLarge = headline1,
        headlineMedium = headline2,
        titleLarge = subtitle1,
        titleMedium = subtitle2,
        bodyLarge = body1,
        bodyMedium = body2,
    )