package com.example.presentation.features.main.statistics.models

import androidx.compose.ui.graphics.Color
import com.himanshoe.charty.common.ChartColor

data class ChartNutrientInfo(
    val label: String,
    val grams: Int,
    val percent: Int,
    val chartColor: ChartColor,
    val legendColor: Color
)