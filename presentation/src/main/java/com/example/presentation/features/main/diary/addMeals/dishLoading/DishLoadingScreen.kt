package com.example.presentation.features.main.diary.addMeals.dishLoading

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.HandleError
import java.time.LocalDate

@Composable
fun DishLoadingRoute(
    mealType: MealType,
    date: LocalDate,
    imgUri: String,
    onBackPressed: () -> Unit,
    viewModel: DishLoadingVM = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()

    DishLoadingScreen(
        baseUiState = baseUiState,
        imgUri = imgUri,
        dishes = uiState.dishes,
        onErrorConsume = { viewModel.clearErrors() },
        onConnectionRetry = { viewModel.retryLastAction() }
    )
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun DishLoadingScreen(
    baseUiState: BaseUiState,
    imgUri: String,
    dishes: List<Dish>,
    onErrorConsume: () -> Unit,
    onConnectionRetry: () -> Unit,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val boxSize = screenWidth * 0.7f

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = imgUri,
                contentDescription = null,
                modifier = Modifier
                    .size(boxSize)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(24.dp))

            when {
                baseUiState.isLoading -> {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Аналізуємо фото…")
                }

                dishes.isEmpty() -> {
                    Text("На фото не знайдено їжі", color = Color.Gray)
                }

                else -> {
                    Text("Знайдено страви: ${dishes.size}")
                }
            }
        }

        HandleError(
            baseUiState = baseUiState,
            onErrorConsume = onErrorConsume,
            onConnectionRetry = onConnectionRetry
        )
    }
}