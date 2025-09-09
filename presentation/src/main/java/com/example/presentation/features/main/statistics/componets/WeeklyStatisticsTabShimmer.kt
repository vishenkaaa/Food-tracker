package com.example.presentation.features.main.statistics.componets

import android.annotation.SuppressLint
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.presentation.common.ui.modifiers.shimmerEffect

@Composable
fun WeeklyStatisticsTabShimmer(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        item {
            WeekNavigationShimmer()
        }

        item {
            WeeklyBarChartShimmer()
        }

        item {
            WeeklyNutritionAverageShimmer()
        }

        item {
            MacronutrientPieChartShimmer()
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun WeekNavigationShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .height(46.dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .shimmerEffect(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
    }
}

@Composable
fun WeeklyBarChartShimmer() {
    Column {
        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .width(160.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(16.dp))
                .shimmerEffect()
        )

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .shimmerEffect()
        )
    }
}

@Composable
fun WeeklyNutritionAverageShimmer() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .width(160.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(16.dp))
                .shimmerEffect()
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(4) {
                NutritionPillShimmer()
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun NutritionPillShimmer() {
    val itemHeight = 160.dp
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(width = 54.dp, height = itemHeight)
                .clip(RoundedCornerShape(40.dp))
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(40.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .shimmerEffect()
        )
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MacronutrientPieChartShimmer() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val boxSize = screenWidth * 0.6f

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(boxSize)
                .clip(CircleShape)
                .shimmerEffect()
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(end = 8.dp)
        ) {
            repeat(3) {
                LegendItemShimmer()
            }
        }
    }
}

@Composable
fun LegendItemShimmer() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .height(18.dp)
                .width(36.dp)
                .clip(CircleShape)
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .width(70.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .shimmerEffect()
        )
    }
}