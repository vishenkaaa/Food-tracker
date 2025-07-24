package com.example.presentation.common.values

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.presentation.R

val MainAccentColor: Color
    @Composable
    get() = colorResource(R.color.main_accent_color)

val White: Color
    @Composable
    get() = colorResource(R.color.white)

val White10: Color
    @Composable
    get() = colorResource(R.color.white_10)

val White50: Color
    @Composable
    get() = colorResource(R.color.white_50)

val DarkColor: Color
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

val Black: Color
    @Composable
    get() = colorResource(R.color.black)