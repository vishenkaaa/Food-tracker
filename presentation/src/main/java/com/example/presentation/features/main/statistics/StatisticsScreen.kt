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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.statistics.StatisticsPeriod
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.CenterAlignedHeader
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.extensions.displayName
import com.example.presentation.features.main.statistics.componets.DailyStatisticsTab
import com.example.presentation.features.main.statistics.componets.DailyStatisticsTabShimmer
import com.example.presentation.features.main.statistics.componets.WeeklyStatisticsTab
import com.example.presentation.features.main.statistics.componets.WeeklyStatisticsTabShimmer
import com.example.presentation.features.main.statistics.models.StatisticsUiState
import kotlinx.coroutines.launch

@Composable
fun StatisticsRoute(
    viewModel: StatisticsVM = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()
    val isDailyLoading by viewModel.isDailyLoading.collectAsStateWithLifecycle()
    val isWeeklyLoading by viewModel.isWeeklyLoading.collectAsStateWithLifecycle()

    StatisticsScreen(
        uiState = uiState,
        baseUiState = baseUiState,
        isDailyLoading = isDailyLoading,
        isWeeklyLoading = isWeeklyLoading,
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
    isDailyLoading: Boolean,
    isWeeklyLoading: Boolean,
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
                        thickness = 0.5.dp,
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
                        if (isDailyLoading || uiState.dailyStatistics == null)
                            DailyStatisticsTabShimmer()
                        else
                            DailyStatisticsTab(
                                statistics = uiState.dailyStatistics
                            )
                    }

                    StatisticsPeriod.WEEK -> {
                        if (isWeeklyLoading || uiState.weeklyStatistics == null)
                            WeeklyStatisticsTabShimmer()
                        else
                            WeeklyStatisticsTab(
                                statistics = uiState.weeklyStatistics,
                                onPreviousWeek = onPreviousWeek,
                                onNextWeek = onNextWeek
                            )
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


@Composable
fun StatisticsTabRow(
    selectedTab: StatisticsPeriod,
    onTabSelected: (StatisticsPeriod) -> Unit,
) {
    PrimaryTabRow(
        selectedTabIndex = StatisticsPeriod.entries.indexOf(selectedTab),
        containerColor = Color.Transparent,
        divider = { },
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        StatisticsPeriod.entries.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.displayName(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedTab == tab) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary
                    )
                }
            )
        }
    }
}