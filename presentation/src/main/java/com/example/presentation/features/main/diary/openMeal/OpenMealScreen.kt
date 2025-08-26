package com.example.presentation.features.main.diary.openMeal

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.ConfirmationDialog
import com.example.presentation.features.main.diary.editDish.EditDishBottomSheet
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.LeftAlignedHeader
import com.example.presentation.common.ui.components.RoundedCircularProgress
import com.example.presentation.common.ui.modifiers.softShadow
import com.example.presentation.extensions.displayName
import com.example.presentation.features.main.diary.DiaryVM
import com.example.presentation.features.main.diary.components.MacroNutrientsBigSection
import com.example.presentation.features.main.diary.components.MacroNutrientsSmallSection
import com.example.presentation.features.main.diary.openMeal.models.OpenMealUIState
import java.time.LocalDate

@Composable
fun OpenMealRoute(
    diaryVM: DiaryVM = hiltViewModel(),
    viewModel: OpenMealVM = hiltViewModel(),
    mealType: MealType,
    dishes: List<Dish>,
    date: LocalDate,
    targetCalories: Int,
    onBackPressed: () -> Unit,
    onNavigateToAddDish: (MealType, LocalDate) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()

    LaunchedEffect(mealType, dishes, date, targetCalories) {
        viewModel.initializeMeal(mealType, dishes, date, targetCalories)
    }

    BackHandler {
        onBackPressed()
    }

    OpenMealScreen(
        uiState = uiState,
        baseUiState = baseUiState,
        onBackPressed = onBackPressed,
        onAddDishClick = { onNavigateToAddDish(mealType, date) },
        onEditDish = { dish -> viewModel.onEditDish(dish) },
        onSaveEditedDish = { dish, newMealType -> viewModel.onSaveEditedDish(dish, newMealType, diaryVM) },
        onEditDishDismiss = { viewModel.onEditDishDismiss() },
        onDeleteDish = viewModel::requestDeleteConfirmation,
        onDeleteConfirmationResult = { status -> viewModel.onDeleteConfirmationResult(status, diaryVM) },
        onErrorConsume = { viewModel.clearErrors() },
        onRetry = { viewModel.retryLastAction() }
    )
}

@Composable
fun OpenMealScreen(
    uiState: OpenMealUIState,
    baseUiState: BaseUiState,
    onBackPressed: () -> Unit,
    onAddDishClick: () -> Unit,
    onEditDish: (Dish) -> Unit,
    onSaveEditedDish: (Dish, MealType) -> Unit,
    onEditDishDismiss: () -> Unit,
    onDeleteConfirmationResult: (Boolean) -> Unit,
    onDeleteDish: (String) -> Unit,
    onErrorConsume: () -> Unit,
    onRetry: () -> Unit,
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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddDishClick,
                    modifier = Modifier
                        .size(70.dp),
                    shape = RoundedCornerShape(50.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.big_plus),
                        contentDescription = "Add dish",
                        Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center
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
                    OpenMealNutritionSection(
                        calories = uiState.calories,
                        targetCalories = uiState.targetCalories,
                        carb = uiState.carbs,
                        protein = uiState.protein,
                        fat = uiState.fat
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_dishes_added),
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
                        OpenMealNutritionSection(
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

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
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
fun OpenMealNutritionSection(
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
        Box(
            modifier = Modifier.size(161.dp),
            contentAlignment = Alignment.Center
        ) {
            val progress = if (targetCalories > 0) {
                (calories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }

            RoundedCircularProgress(
                progress = progress,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(150.dp)),
                strokeWidth = 16.dp,
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.lightning),
                    contentDescription = "Energy",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = MaterialTheme.typography.bodyLarge.toSpanStyle()
                                .copy(color = MaterialTheme.colorScheme.onBackground)
                        ) {
                            append(calories.toString())
                        }
                        withStyle(
                            style = MaterialTheme.typography.titleSmall.toSpanStyle()
                                .copy(color = MaterialTheme.colorScheme.onBackground)
                        ) {
                            append(" " + stringResource(R.string.kcal))
                        }
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        MacroNutrientsBigSection(protein, fat, carb)
    }
}

@Composable
fun SwipeDishItem(
    dish: Dish,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.EndToStart -> {
                onRemove()
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }

            SwipeToDismissBoxValue.StartToEnd -> {
                onEdit()
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }

            SwipeToDismissBoxValue.Settled -> Unit
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier.softShadow(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
            blurRadius = 12.dp,
            offsetY = 1.dp,
            offsetX = 1.dp,
            cornerRadius = 16.dp
        ),
        backgroundContent = { SwipeBackground(dismissState = dismissState) },
        content = {
            DishCard(dish)
        }
    )
}

@Composable
private fun SwipeBackground(dismissState: SwipeToDismissBoxState) {
    val alignment = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.Center
    }

    val icon = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> painterResource(R.drawable.pencil)
        SwipeToDismissBoxValue.EndToStart -> painterResource(R.drawable.trash)
        else -> null
    }

    val scale by animateFloatAsState(
        targetValue = if (
            dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd ||
            dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart
        ) 1.3f else 1f,
        animationSpec = tween(400)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 24.dp),
        contentAlignment = alignment
    ) {
        icon?.let {
            Icon(
                painter = it,
                contentDescription = "Action",
                tint = Color.Unspecified,
                modifier = Modifier
                    .scale(scale)
                    .size(24.dp)
            )
        }
    }
}

@Composable
private fun DishCard(
    dish: Dish,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth()
            .softShadow(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                blurRadius = 12.dp,
                offsetY = 1.dp,
                offsetX = 1.dp,
                cornerRadius = 16.dp
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = dish.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.kcal_value, dish.kcal),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "${dish.amount} ${dish.unit.displayName()}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.onBackground.copy(0.1f))
            )

            MacroNutrientsSmallSection(dish.protein, dish.fats, dish.carb)
        }
    }
}