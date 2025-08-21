package com.example.presentation.features.main.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.statistics.DailyNutritionStatistics
import com.example.domain.model.statistics.NutritionStatistics
import com.example.domain.model.statistics.StatisticsPeriod
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.CenterAlignedHeader
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.features.main.statistics.componets.DailyStatisticsTab
import com.example.presentation.features.main.statistics.componets.StatisticsTabRow
import com.example.presentation.features.main.statistics.componets.WeeklyStatisticsTab
import kotlinx.coroutines.launch

@Composable
fun StatisticsRoute(
    viewModel: StatisticsVM = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()

    StatisticsScreen(
        uiState = uiState,
        baseUiState = baseUiState,
        onPeriodSelected = viewModel::onPeriodSelected,
        onPreviousWeek = viewModel::onPreviousWeek,
        onNextWeek = viewModel::onNextWeek,
        onRetry = viewModel::loadStatistics,
        onErrorConsume = viewModel::clearErrors
    )
}

@Composable
fun StatisticsScreen(
    uiState: StatisticsUiState,
    baseUiState: BaseUiState,
    onPeriodSelected: (StatisticsPeriod) -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onRetry: () -> Unit,
    onErrorConsume: () -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = StatisticsPeriod.entries.indexOf(uiState.selectedPeriod),
        pageCount = { 3 }
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.selectedPeriod) {
        val targetPage = StatisticsPeriod.entries.indexOf(uiState.selectedPeriod)
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        val newPeriod = StatisticsPeriod.entries[pagerState.currentPage]
        if (newPeriod != uiState.selectedPeriod) {
            onPeriodSelected(newPeriod)
        }
    }

    Box{
        Scaffold(
            topBar = {
                Column {
                    CenterAlignedHeader(stringResource(R.string.statistics))

                    StatisticsTabRow(
                        selectedTab = uiState.selectedPeriod,
                        onTabSelected = { period ->
                            onPeriodSelected(period)
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    StatisticsPeriod.entries.indexOf(period)
                                )
                            }
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.onBackground.copy(0.2f))
                    )
                }
            }
        ) { padding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalAlignment = Alignment.Top
            ) { page ->
                when (StatisticsPeriod.entries[page]) {
                    StatisticsPeriod.TODAY,
                    StatisticsPeriod.YESTERDAY -> {
                        if (baseUiState.isLoading) {
                            DailyStatisticsTab(
                                statistics = DailyNutritionStatistics(),
                                loading = true
                            )
                        } else if (uiState.nutritionStatistics is NutritionStatistics.Daily) {
                            DailyStatisticsTab(
                                statistics = uiState.nutritionStatistics.data,
                                loading = false
                            )
                        }
                    }

                    StatisticsPeriod.WEEK -> {
                        if (baseUiState.isLoading) {
                            DailyStatisticsTab(
                                statistics = DailyNutritionStatistics(),
                                loading = true
                            )
                        } else if (uiState.nutritionStatistics is NutritionStatistics.Weekly) {
                            WeeklyStatisticsTab(
                                statistics = uiState.nutritionStatistics.data,
                                loading = false,
                                onPreviousWeek = { onPreviousWeek() },
                                onNextWeek = { onNextWeek() }
                            )
                        }
                    }
                }
            }
        }

        HandleError(
            baseUiState = baseUiState,
            onErrorConsume = onErrorConsume,
            onConnectionRetry = onRetry
        )
    }
}