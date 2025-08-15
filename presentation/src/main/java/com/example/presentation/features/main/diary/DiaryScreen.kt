package com.example.presentation.features.main.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.diary.MealType
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.RoundedCircularProgress
import com.example.presentation.common.utils.getAppLocale
import com.example.presentation.extensions.displayName
import com.example.presentation.extensions.icon
import com.example.presentation.common.ui.modifiers.shimmerEffect
import com.example.presentation.common.ui.modifiers.softShadow
import com.example.presentation.features.main.diary.models.DiaryScreenUIState
import com.example.presentation.features.main.diary.models.getMealsForDate
import com.example.presentation.features.main.diary.models.getNutritionForMealType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@Composable
fun DiaryRoute(
    viewModel: DiaryVM = hiltViewModel(),
    onNavigateToMealDetails: (MealType, LocalDate) -> Unit = { _, _ -> },
    onNavigateToAddMeal: (MealType, LocalDate) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()

    DiaryScreen(
        uiState = uiState,
        baseUiState = baseUiState,
        onPreviousWeek = { viewModel.onPreviousWeek() },
        onNextWeek = { viewModel.onNextWeek() },
        onDateSelected = viewModel::onDateSelected,
        onMealItemClick = { mealType ->
            onNavigateToMealDetails(mealType, uiState.selectedDate)
        },
        onAddMealClick = { mealType ->
            onNavigateToAddMeal(mealType, uiState.selectedDate)
        },
        onErrorConsume = { viewModel.clearErrors() },
        onConnectionRetry = { viewModel.retryLastAction() }
    )
}

@Composable
fun DiaryScreen(
    uiState: DiaryScreenUIState,
    baseUiState: BaseUiState,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onMealItemClick: (MealType) -> Unit = {},
    onAddMealClick: (MealType) -> Unit = {},
    onErrorConsume: () -> Unit,
    onConnectionRetry: () -> Unit
) {
    val isLoading = baseUiState.isLoading
    val hasError = baseUiState.unexpectedError != null || baseUiState.isConnectionError

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                CalendarHeader(uiState.weekStart, onPreviousWeek, onNextWeek)
                CalendarSection(
                    weekStart = uiState.weekStart,
                    selectedDate = uiState.selectedDate,
                    onDateSelected = onDateSelected,
                    uiState = uiState,
                    baseUiState = baseUiState
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                if (isLoading || hasError) CaloriesProgressSectionShimmer()
                else CaloriesProgressSection(
                    caloriesConsumed = uiState.caloriesConsumed,
                    caloriesTarget = uiState.caloriesTarget,
                    carbs = uiState.carbs,
                    protein = uiState.protein,
                    fat = uiState.fat
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                if (isLoading || hasError) MealsSectionShimmer()
                else MealsSection(
                    uiState = uiState,
                    onMealItemClick = onMealItemClick,
                    onAddMealClick = onAddMealClick
                )
                Spacer(Modifier.height(90.dp))
            }
        }

        HandleError(
            baseUiState = baseUiState,
            onErrorConsume = onErrorConsume,
            onConnectionRetry = onConnectionRetry
        )
    }
}

@Composable
fun MealsSection(
    uiState: DiaryScreenUIState,
    onMealItemClick: (MealType) -> Unit,
    onAddMealClick: (MealType) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        MealType.entries.forEach { mealType ->
            val nutrition = uiState.getNutritionForMealType(mealType)
            MealItem(
                mealType = mealType,
                calories = nutrition.calories,
                carbs = nutrition.carbs,
                protein = nutrition.protein,
                fat = nutrition.fat,
                onItemClick = { onMealItemClick(mealType) },
                onAddClick = { onAddMealClick(mealType) }
            )
        }
    }
}

@Composable
fun MealsSectionShimmer() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        repeat(3) {
            MealItemShimmer()
        }
    }
}

@Composable
fun MealItem(
    mealType: MealType,
    calories: Int,
    carbs: Int,
    protein: Int,
    fat: Int,
    onItemClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        onClick = onItemClick,
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
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = mealType.icon(),
                    contentDescription = mealType.value,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = mealType.displayName(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.width(12.dp))
                if (calories != 0)
                    Text(
                        text = "$calories kcal",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = onAddClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.plus),
                        contentDescription = "Add",
                        tint = Color.Unspecified
                    )
                }
            }
            if (calories != 0) {
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    MacroNutrientsItem(stringResource(R.string.carb), carbs)
                    MacroNutrientsItem(stringResource(R.string.protein), protein)
                    MacroNutrientsItem(stringResource(R.string.fat), fat)
                    Spacer(Modifier.weight(1f))

                    Icon(
                        painter = painterResource(R.drawable.dots_three_vertical),
                        contentDescription = "more",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MealItemShimmer() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shimmerEffect()
        )
    }
}


@Composable
fun MacroNutrientsItem(
    type: String,
    value: Int
) {
    Row {
        Text(
            text = type,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.g, value),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun CaloriesProgressSection(
    caloriesConsumed: Int,
    caloriesTarget: Int,
    carbs: Int,
    protein: Int,
    fat: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(214.dp),
            contentAlignment = Alignment.Center
        ) {
            val progress = (caloriesConsumed.toFloat() / caloriesTarget.toFloat()).coerceIn(0f, 1f)

            RoundedCircularProgress(
                progress = progress,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(150.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(0.05f)),
                strokeWidth = 20.dp,
                progressColor = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.lightning),
                    contentDescription = "Energy",
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "$caloriesConsumed/$caloriesTarget",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.kcal),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MacroNutrientItem(
                label = stringResource(R.string.carb),
                value = stringResource(R.string.g, carbs),
            )

            MacroNutrientItem(
                label = stringResource(R.string.protein),
                value = stringResource(R.string.g, protein),
            )

            MacroNutrientItem(
                label = stringResource(R.string.fat),
                value = stringResource(R.string.g, fat),
            )
        }
    }
}

@Composable
fun CaloriesProgressSectionShimmer() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(150.dp))
                .size(214.dp)
                .shimmerEffect(),
        )

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MacroNutrientItemShimmer()
            MacroNutrientItemShimmer()
            MacroNutrientItemShimmer()
        }
    }
}

@Composable
fun MacroNutrientItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun MacroNutrientItemShimmer() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .shimmerEffect()
    ) {
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(48.dp)
        )
    }
}

@Composable
fun CalendarSection(
    weekStart: LocalDate,
    selectedDate: LocalDate,
    uiState: DiaryScreenUIState,
    baseUiState: BaseUiState,
    onDateSelected: (LocalDate) -> Unit
) {
    val locale = remember { getAppLocale() }
    val daysOfWeek = remember {
        listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        ).map {
            it.getDisplayName(TextStyle.SHORT, locale)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        daysOfWeek.forEachIndexed { index, dayName ->
            val date = weekStart.plusDays(index.toLong())
            val notEmpty = uiState.getMealsForDate(date)?.let { meals ->
                meals.breakfast.isNotEmpty() ||
                        meals.lunch.isNotEmpty() ||
                        meals.dinner.isNotEmpty() ||
                        meals.snacks.isNotEmpty()
            } ?: false
            val enabled = !baseUiState.isLoading
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                DayItem(
                    dayOfWeek = dayName,
                    day = date.dayOfMonth,
                    enabled = enabled,
                    selected = date == selectedDate,
                    notEmpty = notEmpty,
                    onSelected = { onDateSelected(date) }
                )
            }
        }
    }
}

@Composable
fun DayItem(
    dayOfWeek: String,
    day: Int,
    enabled: Boolean = true,
    selected: Boolean = false,
    notEmpty: Boolean = false,
    onSelected: () -> Unit,
) {
    Card(
        onClick = onSelected,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.background,
            disabledContainerColor = if (!isSystemInDarkTheme()) Color(0xFFEEEEEE) else Color(
                0xFF444444
            )
        ),
        enabled = enabled,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 0.dp
        ),
        modifier = Modifier
            .softShadow(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                blurRadius = 25.dp,
                offsetY = 4.dp,
                offsetX = 0.dp,
                cornerRadius = 20.dp
            )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayOfWeek,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(top = 8.dp)
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = if (selected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onBackground
                )

                if (!selected && notEmpty) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-3).dp)
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(
    weekStart: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    val middleOfWeek = weekStart.plusDays(3)

    val locale = remember { getAppLocale() }

    val formatter = remember(locale) {
        DateTimeFormatter.ofPattern("LLLL yyyy", locale)
    }

    val formattedMonth = middleOfWeek.format(formatter)
        .replaceFirstChar { it.titlecase(locale) }

    Row(
        modifier = Modifier
            .padding(top = 45.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousWeek) {
            Icon(
                painter = painterResource(R.drawable.back),
                contentDescription = "Previous week",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = formattedMonth,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        IconButton(onClick = onNextWeek) {
            Icon(
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = "Next week",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}