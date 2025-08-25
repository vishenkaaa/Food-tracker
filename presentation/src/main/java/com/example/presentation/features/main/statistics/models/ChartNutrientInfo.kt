package com.example.presentation.features.main.statistics.models

import androidx.compose.ui.graphics.Color
import com.himanshoe.charty.common.ChartColor
import com.himanshoe.charty.common.asSolidChartColor

data class ChartNutrientInfo(
    val label: String,
    val grams: Float,
    val percent: Int,
    val color: Color
) {
    fun chartColor(isDark: Boolean): ChartColor =
        if (isDark) color.asSolidChartColor()
        else color.copy(0.2f).asSolidChartColor()

    fun labelColor(isDark: Boolean): ChartColor =
        if (isDark) Color.White.asSolidChartColor()
        else color.asSolidChartColor()

    fun legendColor(isDark: Boolean): Color =
        if (isDark) color else color.copy(0.2f)
}