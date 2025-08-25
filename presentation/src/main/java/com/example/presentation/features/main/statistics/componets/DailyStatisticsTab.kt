package com.example.presentation.features.main.statistics.componets

import android.annotation.SuppressLint
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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.domain.extension.roundTo1Decimal
import com.example.domain.model.statistics.DailyNutritionStatistics
import com.example.domain.model.statistics.MealStatistics
import com.example.domain.model.statistics.StatisticsPeriod
import com.example.presentation.R
import com.example.presentation.common.ui.components.RoundedCircularProgress
import com.example.presentation.extensions.displayName
import com.example.presentation.features.main.diary.components.MacroNutrientsSmallSection
import kotlin.math.roundToInt

@Composable
fun DailyStatisticsTab(
    statistics: DailyNutritionStatistics,
    loading: Boolean = false
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
                .fillMaxWidth(mealStatistics.percentage / 100 .toFloat())
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
            RoundedCircularProgress(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(150.dp)),
                progress = statistics.progress,
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