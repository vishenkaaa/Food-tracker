package com.example.presentation.features.main.navigation

import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
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
    data object Onboarding : LoginGraph()
}


@Serializable
sealed class MainGraph {

    @Serializable
    data object Statistics : MainGraph()

    @Serializable
    data object Dairy : MainGraph()

    @Serializable
    data object Profile : MainGraph()

    @Serializable
    data object DeleteAccount : MainGraph()

    @Serializable
    data class OpenMeal(
        val mealType: MealType,
        val date: String,
        val targetCalories: Int
    ) : MainGraph()
}