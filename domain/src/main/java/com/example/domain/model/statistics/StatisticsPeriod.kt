package com.example.domain.model.statistics

enum class StatisticsPeriod(val value: String) {
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    WEEK("Week")
}

sealed class StatisticsPeriodTab() {
    data object TimeBased : StatisticsPeriodTab()
    data object CounterBased : StatisticsPeriodTab()

    companion object{
        val values = listOf(TimeBased, CounterBased)
        fun fromPage(page: Int) = values[page]
        fun toPage(tab: StatisticsPeriodTab) = values.indexOf(tab)
    }
}