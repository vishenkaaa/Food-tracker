package com.example.presentation.common.values

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.presentation.R

val Green: Color
    @Composable
    get() = colorResource(R.color.green)

val White: Color
    @Composable
    get() = colorResource(R.color.white)

val Green50: Color
    @Composable
    get() = colorResource(R.color.green_50)

val White50: Color
    @Composable
    get() = colorResource(R.color.white_50)

val DarkGreen: Color
    @Composable
    get() = colorResource(R.color.dark_color)

val Gray: Color
    @Composable
    get() = colorResource(R.color.gray)

val GrayLight: Color
    @Composable
    get() = colorResource(R.color.gray_light)

val GrayDark: Color
    @Composable
    get() = colorResource(R.color.gray_dark)

val DarkBackground: Color
    @Composable
    get() = colorResource(R.color.dark_bg)