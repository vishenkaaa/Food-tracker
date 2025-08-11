package com.example.presentation.features.main.navigation

enum class TopLevelDestinations(
    val route: MainGraph,
    val contentDescription: String
) {
    STATISTICS(
        MainGraph.Statistics,
        "Statistics"
    ),
    DIARY(
        MainGraph.Dairy,
        "Diary"
    ),
    PROFILE(
        MainGraph.Profile,
        "Profile"
    )
}