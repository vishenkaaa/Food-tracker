package com.example.presentation.features.main.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Graphs {
    @Serializable
    data object IdleScreen : Graphs()

    @Serializable
    data object Login : Graphs()

    @Serializable
    data object Main : Graphs()
}

@Serializable
sealed class LoginGraph {

    @Serializable
    data object Google : LoginGraph()

    @Serializable
    data object TargetCalories : LoginGraph()
}


@Serializable
sealed class MainGraph {

    @Serializable
    data object Statistics : MainGraph()

    @Serializable
    data object Dairy : MainGraph()

    @Serializable
    data object Profile : MainGraph()
}