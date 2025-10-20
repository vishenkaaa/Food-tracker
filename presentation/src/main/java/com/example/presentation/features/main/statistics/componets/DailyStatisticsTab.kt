package com.example.presentation.features.main.statistics.componets

import android.annotation.SuppressLint
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.domain.model.diary.MealType
import com.example.domain.model.statistics.DailyNutritionStatistics
import com.example.domain.model.statistics.MealStatistics
import com.example.presentation.R
import com.example.presentation.common.ui.components.RoundedCircularProgress
import com.example.presentation.common.ui.values.FoodTrackTheme
import com.example.presentation.extensions.displayName
import com.example.presentation.features.main.diary.components.MacroNutrientsSmallSection
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun DailyStatisticsTab(
    statistics: DailyNutritionStatistics
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DailyNutritionCircularProgress(statistics = statistics)
        }

        items(statistics.mealStatistics) { mealStat ->
            MealStatisticsItem(mealStatistics = mealStat)
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun MealStatisticsItem(
    mealStatistics: MealStatistics,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = mealStatistics.mealType.displayName(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearCaloriesProgress(mealStatistics)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${mealStatistics.percentage}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.kcal_value, mealStatistics.calories),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        MacroNutrientsSmallSection(mealStatistics.protein, mealStatistics.fat, mealStatistics.carbs)
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun LinearCaloriesProgress(mealStatistics: MealStatistics) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val boxSize = screenWidth * 0.7f

    var trigger by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(10)
        trigger = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if(trigger) mealStatistics.percentage / 100.toFloat() else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)
        ),
        label = "progress_animation"
    )

    Box(
        modifier = Modifier
            .height(8.dp)
            .width(boxSize)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
}

@Composable
fun DailyNutritionCircularProgress(
    statistics: DailyNutritionStatistics,
    modifier: Modifier = Modifier
) {
    var trigger by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(10)
        trigger = true
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.nutrition_statistics),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(161.dp)
        ) {
            val animatedProgress by animateFloatAsState(
                targetValue = if(trigger) statistics.progress else 0f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)
                ),
                label = "progress_animation"
            )

            RoundedCircularProgress(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(150.dp)),
                progress = animatedProgress,
                strokeWidth = 15.dp,
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${(statistics.progress * 100).roundToInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${statistics.totalCalories}/${statistics.targetCalories}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.kcal),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyStatisticsTabPreview() {
    val mockMealStats = listOf(
        MealStatistics(
            mealType = MealType.BREAKFAST,
            calories = 350,
            carbs = 40f,
            protein = 15f,
            fat = 10f,
            percentage = 30
        ),
        MealStatistics(
            mealType = MealType.LUNCH,
            calories = 550,
            carbs = 60f,
            protein = 25f,
            fat = 20f,
            percentage = 50
        ),
        MealStatistics(
            mealType = MealType.DINNER,
            calories = 250,
            carbs = 20f,
            protein = 20f,
            fat = 12f,
            percentage = 20
        )
    )

    val mockDailyStats = DailyNutritionStatistics(
        totalCalories = 1150,
        targetCalories = 2000,
        progress = 1150f / 2000f,
        mealStatistics = mockMealStats
    )

    FoodTrackTheme {
        DailyStatisticsTab(
            statistics = mockDailyStats
        )
    }
}
