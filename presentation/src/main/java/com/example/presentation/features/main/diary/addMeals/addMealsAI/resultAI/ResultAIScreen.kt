package com.example.presentation.features.main.diary.addMeals.addMealsAI.resultAI

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.ConfirmationDialog
import com.example.presentation.common.ui.components.CustomButton
import com.example.presentation.features.main.diary.editDish.EditDishBottomSheet
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.LeftAlignedHeader
import com.example.presentation.common.ui.components.RoundedCircularProgress
import com.example.presentation.common.ui.modifiers.softShadow
import com.example.presentation.extensions.displayName
import com.example.presentation.features.main.diary.DiaryVM
import com.example.presentation.features.main.diary.components.CaloriesDisplay
import com.example.presentation.features.main.diary.components.MacroNutrientsBigSection
import com.example.presentation.features.main.diary.components.MacroNutrientsSmallSection
import com.example.presentation.features.main.diary.components.SwipeDishItem
import com.example.presentation.features.main.diary.openMeal.models.OpenMealUIState
import java.time.LocalDate

@Composable
fun ResultAIRoute(
    diaryVM: DiaryVM = hiltViewModel(),
    viewModel: ResultAIVM = hiltViewModel(),
    imgUri: String,
    mealType: MealType,
    dishes: List<Dish>,
    date: LocalDate,
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()

    LaunchedEffect(mealType, dishes, date) {
        viewModel.initializeMeal(mealType, dishes, date, 0)
    }

    ResultAIScreen(
        imgUri = imgUri,
        uiState = uiState,
        baseUiState = baseUiState,
        onBackPressed = onBackPressed,
        onEditDish = { dish -> viewModel.onEditDish(dish) },
        onSaveEditedDish = { dish, newMealType -> viewModel.onSaveEditedDish(dish, newMealType, diaryVM) },
        onEditDishDismiss = { viewModel.onEditDishDismiss() },
        onDeleteDish = viewModel::requestDeleteConfirmation,
        onDeleteConfirmationResult = { status -> viewModel.onDeleteConfirmationResult(status, diaryVM) },
        onErrorConsume = { viewModel.clearErrors() },
        onRetry = { viewModel.retryLastAction() },
        onSave = { viewModel.onSaveDishes(diaryVM) }
    )
}

@Composable
fun ResultAIScreen(
    imgUri: String,
    uiState: OpenMealUIState,
    baseUiState: BaseUiState,
    onBackPressed: () -> Unit,
    onEditDish: (Dish) -> Unit,
    onSaveEditedDish: (Dish, MealType) -> Unit,
    onEditDishDismiss: () -> Unit,
    onDeleteConfirmationResult: (Boolean) -> Unit,
    onDeleteDish: (String) -> Unit,
    onErrorConsume: () -> Unit,
    onRetry: () -> Unit,
    onSave: () -> Unit
) {
    Box {
        Scaffold(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            topBar = {
                LeftAlignedHeader(
                    mealType = uiState.mealType,
                    onNavigateBack = onBackPressed
                )
            },
            bottomBar = {
                CustomButton(
                    text = stringResource(R.string.save),
                    modifier = Modifier.navigationBarsPadding().padding(bottom = 32.dp)
                ) { onSave() }
            }
        ) { padding ->
            if (uiState.dishes.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding()
                        )
                ) {
                    NutritionSection(
                        imgUri = imgUri,
                        calories = uiState.calories,
                        targetCalories = uiState.targetCalories,
                        carb = uiState.carbs,
                        protein = uiState.protein,
                        fat = uiState.fat
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Не вдалось проаналізувати зображення",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        NutritionSection(
                            imgUri = imgUri,
                            calories = uiState.calories,
                            targetCalories = uiState.targetCalories,
                            carb = uiState.carbs,
                            protein = uiState.protein,
                            fat = uiState.fat
                        )
                    }

                    items(
                        items = uiState.dishes,
                        key = { it.id }
                    ) { dish ->
                        SwipeDishItem(
                            dish = dish,
                            onEdit = { onEditDish(dish) },
                            onRemove = { onDeleteDish(dish.id) }
                        )
                    }
                }
            }
        }

        ConfirmationDialog(
            visible = uiState.showDeleteMealDialog,
            title = stringResource(R.string.delete_meal_title),
            message = stringResource(R.string.delete_meal_message),
            confirmButtonText = stringResource(R.string.delete),
            dismissButtonText = stringResource(R.string.cancel),
            onConfirm = { onDeleteConfirmationResult(true) },
            onDismiss = { onDeleteConfirmationResult(false) }
        )

        if (uiState.showEditDishDialog && uiState.dishToEdit != null) {
            EditDishBottomSheet(
                dish = uiState.dishToEdit,
                mealType = uiState.mealType,
                onSave = onSaveEditedDish,
                onDismiss = onEditDishDismiss
            )
        }

        HandleError(
            baseUiState = baseUiState,
            onErrorConsume = onErrorConsume,
            onConnectionRetry = onRetry
        )
    }
}

@Composable
fun NutritionSection(
    imgUri: String,
    calories: Int,
    targetCalories: Int,
    carb: Float,
    protein: Float,
    fat: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = imgUri,
            contentDescription = null,
            modifier = Modifier
                .size(161.dp)
                .clip(RoundedCornerShape(150.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(8.dp))

        CaloriesDisplay(calories)

        Spacer(modifier = Modifier.height(12.dp))

        MacroNutrientsBigSection(protein, fat, carb)
    }
}