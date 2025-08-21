package com.example.presentation.features.main.statistics.componets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.example.domain.model.statistics.DayStatistics
import com.example.domain.model.statistics.WeeklyNutritionStatistics
import com.example.presentation.R
import com.example.presentation.common.ui.components.LoadingBackground
import com.example.presentation.common.utils.getAppLocale
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Position
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@Composable
fun WeeklyStatisticsTab(
    statistics: WeeklyNutritionStatistics,
    loading: Boolean = false,
    onPreviousWeek: () -> Unit = {},
    onNextWeek: () -> Unit = {},
) {
    if (loading) {
       LoadingBackground(loading)
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            item {
                WeekNavigation(
                    weekStart = statistics.weekStart,
                    onPreviousWeek = onPreviousWeek,
                    onNextWeek = onNextWeek
                )
            }

            item {
                WeeklyBarChart(
                    days = statistics.dayStatistics,
                    targetCalories = statistics.targetCalories
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun WeeklyBarChart(
    days: List<DayStatistics>,
    targetCalories: Int,
    modifier: Modifier = Modifier
) {
    val locale = remember { getAppLocale() }
    val daysOfWeek = remember {
        listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        ).map {
            it.getDisplayName(TextStyle.SHORT, locale)
        }
    }

    val model = remember { CartesianChartModelProducer() }
    val values = remember(days) { days.map { it.calories.toFloat() } }

    LaunchedEffect(values) {
        model.runTransaction { columnSeries { series(values) } }
    }

    val columnColor = MaterialTheme.colorScheme.primary
    val column = LineComponent(
        fill = fill(columnColor),
        thicknessDp = 10f,
        shape = CorneredShape.rounded(22.dp)
    )
    val columnProvider = ColumnCartesianLayer.ColumnProvider.series(column)

    val goalLine = LineComponent(
        fill(MaterialTheme.colorScheme.primary.copy(0.7f)),
        thicknessDp = 2f
    )

    val customRangeProvider = object : CartesianLayerRangeProvider {
        override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
            val actualMaxY = maxOf(maxY, targetCalories.toDouble())
            return if (actualMaxY <= 0.0) targetCalories.toDouble() * 1.2 else actualMaxY * 1.11
        }

        override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
            return 0.0
        }
    }

    fun calculateStep(maxValue: Double): Double {
        return when {
            maxValue <= 500 -> 100.0
            maxValue <= 1000 -> 200.0
            maxValue <= 2000 -> 250.0
            maxValue <= 3000 -> 500.0
            maxValue <= 5000 -> 500.0
            else -> 1000.0
        }
    }

    val maxChartValue = remember(values, targetCalories) {
        val maxCalories = values.maxOrNull() ?: 0f
        maxOf(maxCalories.toDouble(), targetCalories.toDouble()) * 1.11
    }

    val yAxisStep = remember(maxChartValue) {
        calculateStep(maxChartValue)
    }

    val chart = rememberCartesianChart(
        rememberColumnCartesianLayer(
            columnProvider = columnProvider,
            dataLabel = rememberTextComponent(
                color = MaterialTheme.colorScheme.onBackground,
                textSize = 10.sp,
                typeface = ResourcesCompat.getFont(LocalContext.current, R.font.gilroy_regular)!!,
            ),
            dataLabelValueFormatter = CartesianValueFormatter { _, v, _ ->
                v.toInt().toString()
            },
            dataLabelPosition = Position.Vertical.Top,
            rangeProvider = customRangeProvider,
        ),
        startAxis = VerticalAxis.rememberStart(
            line = null,
            tick = null,
            label = rememberTextComponent(
                color = MaterialTheme.colorScheme.onBackground,
                textSize = 10.sp,
                typeface = ResourcesCompat.getFont(LocalContext.current, R.font.gilroy_regular)!!
            ),
            valueFormatter = CartesianValueFormatter { _, v, _ -> v.toInt().toString() },
            itemPlacer = VerticalAxis.ItemPlacer.step(step = { yAxisStep })
        ),
        bottomAxis = HorizontalAxis.rememberBottom(
            line = null,
            tick = null,
            guideline = null,
            label = rememberTextComponent(
                color = MaterialTheme.colorScheme.onBackground,
                textSize = 12.sp,
                typeface = ResourcesCompat.getFont(LocalContext.current, R.font.gilroy_regular)!!
                ),
            valueFormatter = CartesianValueFormatter { _, v, _ ->
                daysOfWeek.getOrNull(v.toInt()) ?: ""
            }
        ),
        decorations = listOf(
            HorizontalLine(
                y = { targetCalories.toFloat().toDouble() },
                line = goalLine
            )
        )
    )

    Column {
        Text(
            text = "Weekly Overview",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        )

        CartesianChartHost(
            chart = chart,
            modelProducer = model,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        )
    }
}

@Composable
fun WeekNavigation(
    weekStart: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    val weekEnd = weekStart.plusDays(6)

    val locale = remember { getAppLocale() }

    val dayMonthFormatter = remember(locale) {
        DateTimeFormatter.ofPattern("d MMM", locale)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousWeek) {
            Icon(
                painter = painterResource(R.drawable.back),
                contentDescription = "Previous week",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = "${weekStart.format(dayMonthFormatter)} - ${weekEnd.format(dayMonthFormatter)}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        IconButton(onClick = onNextWeek) {
            Icon(
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = "Next week",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}