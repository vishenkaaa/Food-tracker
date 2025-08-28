package com.example.presentation.features.main.statistics.componets

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.example.domain.model.statistics.DayStatistics
import com.example.domain.model.statistics.WeeklyNutritionStatistics
import com.example.presentation.R
import com.example.presentation.common.utils.getAppLocale
import com.example.presentation.features.main.statistics.models.ChartNutrientInfo
import com.himanshoe.charty.pie.PieChart
import com.himanshoe.charty.pie.model.PieChartData
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
            WeeklyNutritionAverage(
                statistics = statistics
            )
        }

        item {
            MacronutrientPieChart(
                carbs = statistics.averageCarbs,
                protein = statistics.averageProtein,
                fat = statistics.averageFat
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun WeeklyNutritionAverage(
    statistics: WeeklyNutritionStatistics,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.weekly_average),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(top = 24.dp, bottom = 16.dp)
                .fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val caloriePercentage = if (statistics.targetCalories > 0) {
                (statistics.averageCalories.toFloat() / statistics.targetCalories * 100).coerceAtMost(100f)
            } else 0f

            NutritionPill(
                value = statistics.averageCalories.toString(),
                label = stringResource(R.string.calories),
                fillPercentage = caloriePercentage / 100f
            )

            val carbPercentage = if (statistics.maxCarbs > 0) {
                statistics.averageCarbs / statistics.maxCarbs
            } else 0f

            NutritionPill(
                value = statistics.averageCarbs.toString(),
                label = stringResource(R.string.carbs),
                fillPercentage = carbPercentage
            )

            val fatPercentage = if (statistics.maxFat > 0) {
                statistics.averageFat / statistics.maxFat
            } else 0f

            NutritionPill(
                value = statistics.averageFat.toString(),
                label = stringResource(R.string.fat),
                fillPercentage = fatPercentage
            )

            val proteinPercentage = if (statistics.maxProtein > 0) {
                statistics.averageProtein/ statistics.maxProtein
            } else 0f

            NutritionPill(
                value = statistics.averageProtein.toString(),
                label = stringResource(R.string.protein),
                fillPercentage = proteinPercentage
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun NutritionPill(
    value: String,
    label: String,
    fillPercentage: Float,
    modifier: Modifier = Modifier
) {
    val itemHeight = 160.dp
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(width = 54.dp, height = itemHeight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(40.dp)
                    )
            )

            val fillHeight = (itemHeight * fillPercentage.coerceIn(0f, 1f))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(40.dp))
            ){
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(width = 54.dp, height = fillHeight)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(40.dp)
                        )
                )
            }

            val circleSize = 38.dp
            val paddingFromTop = 8.dp
            val minGreenHeightForTopPosition = circleSize + paddingFromTop

            val circleModifier = if (fillHeight >= minGreenHeightForTopPosition) {
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = fillHeight - circleSize - paddingFromTop)
                    .size(circleSize)
            } else {
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = paddingFromTop)
                    .size(circleSize)
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = circleModifier
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    )
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MacronutrientPieChart(
    carbs: Float,
    protein: Float,
    fat: Float,
    modifier: Modifier = Modifier
) {
    val nutrients = createNutrients(carbs, protein, fat)
    if (nutrients.isEmpty()) return

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val boxSize = screenWidth * 0.6f
    val isDark = isSystemInDarkTheme()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PieChart(
            modifier = Modifier.size(boxSize),
            data = {
                nutrients.map { nutrient ->
                    PieChartData(
                        value = nutrient.percent.toFloat(),
                        color = nutrient.chartColor(isDark),
                        label = "${nutrient.percent}%",
                        labelColor = nutrient.labelColor(isDark)
                    )
                }
            },
            isDonutChart = false,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(end = 8.dp)
        ) {
            nutrients.forEach { nutrient ->
                LegendItem(
                    color = nutrient.legendColor(isDark),
                    label = nutrient.label,
                    percentage = nutrient.percent,
                    grams = nutrient.grams
                )
            }
        }
    }
}

@Composable
private fun createNutrients(
    carbs: Float,
    protein: Float,
    fat: Float
): List<ChartNutrientInfo> {
    val total = carbs + protein + fat
    if (total <= 0) return emptyList()

    val isDark = isSystemInDarkTheme()
    val carbsColor = if (isDark) Color(0xFFE55367) else Color(0xFFFF003A)
    val proteinColor = if (isDark) Color(0xFF05285F) else Color(0xFF0099FF)
    val fatColor = if (isDark) Color(0xFFEDBF30) else Color(0xFFFDC900)

    val percentages = calculateCorrectedPercentages(listOf(carbs, protein, fat), total)

    return listOf(
        ChartNutrientInfo(
            label = stringResource(R.string.carbs),
            grams = carbs,
            percent = percentages[0],
            color = carbsColor
        ),
        ChartNutrientInfo(
            label = stringResource(R.string.protein),
            grams = protein,
            percent = percentages[1],
            color = proteinColor
        ),
        ChartNutrientInfo(
            label = stringResource(R.string.fat),
            grams = fat,
            percent = percentages[2],
            color = fatColor
        )
    )
}

private fun calculateCorrectedPercentages(values: List<Float>, total: Float): List<Int> {
    if (total <= 0) return List(values.size) { 0 }

    val rawPercentages = values.map { (it / total * 100).toInt() }
    val currentSum = rawPercentages.sum()

    return if (currentSum != 100) {
        val difference = 100 - currentSum
        val maxIndex = values.indices.maxByOrNull { values[it] } ?: 0
        rawPercentages.mapIndexed { index, percent ->
            if (index == maxIndex) percent + difference else percent
        }
    } else {
        rawPercentages
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String,
    percentage: Int,
    grams: Float,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .height(18.dp)
                .width(36.dp)
                .background(color = color, shape = CircleShape)
        )

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.grams_format, grams),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
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
            text = stringResource(R.string.weekly_overview),
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