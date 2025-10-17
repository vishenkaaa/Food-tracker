package com.example.presentation.features.main.diary

import android.Manifest
import android.widget.Toast
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.ConnectionErrorSnackBar
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.RoundedCircularProgress
import com.example.presentation.common.ui.modifiers.shimmerEffect
import com.example.presentation.common.ui.modifiers.softShadow
import com.example.presentation.common.ui.values.FoodTrackTheme
import com.example.presentation.common.utils.getAppLocale
import com.example.presentation.extensions.displayName
import com.example.presentation.extensions.icon
import com.example.presentation.features.main.diary.components.MacroNutrientItemShimmer
import com.example.presentation.features.main.diary.components.MacroNutrientsBigSection
import com.example.presentation.features.main.diary.components.MacroNutrientsSmallSection
import com.example.presentation.features.main.diary.extensions.findActivity
import com.example.presentation.features.main.diary.extensions.getDishesForMealType
import com.example.presentation.features.main.diary.extensions.getMealsForDate
import com.example.presentation.features.main.diary.extensions.getNutritionForMealType
import com.example.presentation.features.main.diary.models.DiaryScreenUIState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DiaryRoute(
    viewModel: DiaryVM = hiltViewModel(),
    onNavigateToOpenMeal: (MealType, List<Dish>, LocalDate, Int) -> Unit = { _, _, _, _ -> },
    onNavigateToAddMeal: (MealType, LocalDate) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()
    val cameraPermissionState by viewModel.cameraPermissionState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val cameraPermissionLauncher = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    ) { granted ->
        val permanentlyDenied = !granted && !ActivityCompat.shouldShowRequestPermissionRationale(
            context.findActivity(),
            Manifest.permission.CAMERA
        )
        viewModel.onCameraPermissionResult(granted, permanentlyDenied)
    }

    LaunchedEffect(cameraPermissionState.shouldRequest) {
        if (cameraPermissionState.shouldRequest) {
            cameraPermissionLauncher.launchPermissionRequest()
            viewModel.resetCameraPermissionRequest()
        }
    }

    LaunchedEffect(cameraPermissionState.hasPermission, cameraPermissionState.pendingMealType) {
        if (cameraPermissionState.hasPermission && cameraPermissionState.pendingMealType != null) {
            onNavigateToAddMeal(cameraPermissionState.pendingMealType!!, uiState.selectedDate)
            viewModel.clearPendingNavigation()
        }
    }

    DiaryScreen(
        uiState = uiState,
        baseUiState = baseUiState,
        onPreviousWeek = { viewModel.onPreviousWeek() },
        onNextWeek = { viewModel.onNextWeek() },
        onDateSelected = viewModel::onDateSelected,
        onMealItemClick = { mealType, dishes ->
            onNavigateToOpenMeal(
                mealType,
                dishes,
                uiState.selectedDate,
                uiState.caloriesTarget
            )
        },
        onAddMealClick = { mealType ->
            viewModel.checkCameraPermission()
            when {
                cameraPermissionState.hasPermission -> onNavigateToAddMeal(mealType, uiState.selectedDate)
                cameraPermissionState.permanentlyDenied -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.request_camera_permission),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> viewModel.requestCameraPermissions(mealType)
            }
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
    onMealItemClick: (MealType, List<Dish>) -> Unit,
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
                    carb = uiState.carb,
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
            }

            item {
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
    onMealItemClick: (MealType, List<Dish>) -> Unit,
    onAddMealClick: (MealType) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        MealType.entries.forEach { mealType ->
            val nutrition = uiState.getNutritionForMealType(mealType)
            val dishes = uiState.getDishesForMealType(uiState.selectedDate, mealType)
            MealItem(
                mealType = mealType,
                calories = nutrition.calories,
                carb = nutrition.carb,
                protein = nutrition.protein,
                fat = nutrition.fat,
                onItemClick = {
                    if (dishes.isEmpty())
                        onAddMealClick(mealType)
                    else
                        onMealItemClick(mealType, dishes)
                },
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
    carb: Float,
    protein: Float,
    fat: Float,
    onItemClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        onClick = onItemClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .softShadow(
                color = MaterialTheme.colorScheme.surfaceVariant,
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
                        text = stringResource(R.string.kcal_value, calories),
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
                    MacroNutrientsSmallSection(protein, fat, carb)

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
fun CaloriesProgressSection(
    caloriesConsumed: Int,
    caloriesTarget: Int,
    carb: Float,
    protein: Float,
    fat: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(214.dp),
            contentAlignment = Alignment.Center
        ) {
            val progress = (caloriesConsumed.toFloat() / caloriesTarget.toFloat()).coerceIn(0f, 1f)

            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)
                ),
                label = "progress_animation"
            )

            RoundedCircularProgress(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(150.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(0.05f)),
                strokeWidth = 20.dp,
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

        MacroNutrientsBigSection(protein, fat, carb)
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
fun CalendarSection(
    weekStart: LocalDate,
    selectedDate: LocalDate,
    uiState: DiaryScreenUIState,
    baseUiState: BaseUiState,
    onDateSelected: (LocalDate) -> Unit
) {
    val locale = remember { getAppLocale() }
    val today = remember { LocalDate.now() }

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
            val isFutureDate = date.isAfter(today)
            val enabled = !baseUiState.isLoading && !isFutureDate
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
            else MaterialTheme.colorScheme.surfaceContainer,
            disabledContainerColor =  if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceContainer,
        ),
        enabled = enabled,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 0.dp
        ),
        modifier = Modifier
            .softShadow(
                color = MaterialTheme.colorScheme.surfaceVariant,
                blurRadius = 25.dp,
                offsetY = 4.dp,
                offsetX = 0.dp,
                cornerRadius = 20.dp
            )
            .alpha(if (enabled) 1f else 0.4f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayOfWeek,
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    selected && enabled -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.onBackground
                },
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
                    color = when {
                        selected && enabled -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onBackground
                    },
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
            .statusBarsPadding()
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

@Preview(showBackground = true)
@Composable
fun DiaryScreenPreview() {
    val mockUiState = DiaryScreenUIState(
        weekStart = LocalDate.now().with(DayOfWeek.MONDAY),
        selectedDate = LocalDate.now(),
        caloriesConsumed = 1200,
        caloriesTarget = 2000,
        carb = 150f,
        protein = 80f,
        fat = 50f,
    )

    val mockBaseUiState = BaseUiState(
        isLoading = false,
        isConnectionError = false,
        unexpectedError = null
    )

    FoodTrackTheme {
        DiaryScreen(
            uiState = mockUiState,
            baseUiState = mockBaseUiState,
            onPreviousWeek = {},
            onNextWeek = {},
            onDateSelected = {},
            onMealItemClick = { _, _ -> },
            onAddMealClick = {},
            onErrorConsume = {},
            onConnectionRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MealItemPreview() {
    FoodTrackTheme {
        MealItem(
            mealType = MealType.LUNCH,
            calories = 450,
            carb = 55f,
            protein = 15f,
            fat = 18f,
            onItemClick = {},
            onAddClick = {}
        )
    }
}
