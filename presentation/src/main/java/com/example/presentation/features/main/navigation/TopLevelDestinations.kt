package com.example.presentation.features.main.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestinations(
    val route: MainGraph,
    val iconSelected: ImageVector,
    val icon: ImageVector,
    val title: String
) {
    STATISTICS(
        MainGraph.Statistics,
        Icons.Default.QueryStats,
        Icons.Default.QueryStats,
        "Statistics"
    ),
    DIARY(
        MainGraph.Dairy,
        Icons.Default.NoteAlt,
        Icons.Default.NoteAlt,
        "Diary"
    ),
    PROFILE(
        MainGraph.Profile,
        Icons.Default.VerifiedUser,
        Icons.Default.VerifiedUser,
        "Profile"
    )
}