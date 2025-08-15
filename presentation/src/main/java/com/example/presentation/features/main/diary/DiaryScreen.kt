package com.example.presentation.features.main.diary

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.diary.MealType
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.RoundedCircularProgress
import com.example.presentation.common.utils.getAppLocale
import com.example.presentation.extensions.displayName
import com.example.presentation.extensions.icon
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@Composable
fun DiaryRoute(
    viewModel: DiaryVM = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()

    DiaryScreen(
        uiState = uiState,
        baseUiState = baseUiState,
        onPreviousWeek = { viewModel.onPreviousWeek() },
        onNextWeek = { viewModel.onNextWeek() },
        onDateSelected = viewModel::onDateSelected
    )
}

@Composable
fun DiaryScreen(
    uiState: DiaryScreenUIState,
    baseUiState: BaseUiState,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalendarHeader(uiState.weekStart, onPreviousWeek, onNextWeek)
        CalendarGrid(
            weekStart = uiState.weekStart,
            selectedDate = uiState.selectedDate,
            onDateSelected = onDateSelected
        )
        Spacer(modifier = Modifier.height(20.dp))

        CaloriesProgressCard(
            caloriesConsumed = uiState.caloriesConsumed,
            caloriesTarget = uiState.caloriesTarget,
            carbs = uiState.carbs,
            protein = uiState.protein,
            fat = uiState.fat
        )
        Spacer(modifier = Modifier.height(20.dp))

        MealsSection()
    }
}

@Composable
fun MealsSection() {
    Column (
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        MealType.entries.forEach{ meal ->
            MealItem(meal, 0, 11, 11, 11, {}, {})
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
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        ),
        modifier = Modifier.fillMaxWidth()
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
                if(calories!=0)
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
            if(calories!=0) {
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    MacroNutrientsItem("Carb", carbs)
                    MacroNutrientsItem("Protein", protein)
                    MacroNutrientsItem("Fat", fat)
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
            text = "${value}g",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun CaloriesProgressCard(
    caloriesConsumed: Int,
    caloriesTarget: Int,
    carbs: Int,
    protein: Int,
    fat: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(214.dp),
            contentAlignment = Alignment.Center
        ) {
            val progress = (caloriesConsumed.toFloat() / caloriesTarget.toFloat()).coerceIn(0f, 1f)

            RoundedCircularProgress(
                progress = progress,
                modifier = Modifier.fillMaxSize()
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
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "$caloriesConsumed/$caloriesTarget",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "kcal",
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
                label = "Carb",
                value = "${carbs}g",
            )

            MacroNutrientItem(
                label = "Protein",
                value = "${protein}g",
            )

            MacroNutrientItem(
                label = "Fat",
                value = "${fat}g",
            )
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
fun CalendarGrid(
    weekStart: LocalDate,
    selectedDate: LocalDate,
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
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                DayItem(
                    dayOfWeek = dayName,
                    day = date.dayOfMonth,
                    selected = date == selectedDate,
                    marked = true,
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
    selected: Boolean = false,
    marked: Boolean = false,
    onSelected: () -> Unit,
) {
    Card(
        onClick = onSelected,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
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
                    .clip(RoundedCornerShape(999.dp))
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

                if (marked) {
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