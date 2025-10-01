package com.example.presentation.features.main.diary.addMeals.addMealsAI.dishLoading

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.presentation.R
import com.example.presentation.common.ui.values.FoodTrackTheme
import java.time.LocalDate

@Composable
fun DishLoadingRoute(
    imgUri: String,
    onBackPressed: () -> Unit,
    onNavigateToResults: (List<Dish>) -> Unit,
    viewModel: DishLoadingVM = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.analyze(imgUri)
    }

    BackHandler {
        onBackPressed()
    }

    LaunchedEffect(uiState.loading) {
        if (!uiState.loading) {
            onNavigateToResults(uiState.dishes)
        }
    }

    DishLoadingScreen()
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun DishLoadingScreen() {
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            RotatingLoadingIndicator()

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun RotatingLoadingIndicator(
    size: Dp = 80.dp,
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("loader_animation.json")
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = "AI",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DishLoadingScreenPreview() {
    FoodTrackTheme {
        DishLoadingScreen()
    }
}