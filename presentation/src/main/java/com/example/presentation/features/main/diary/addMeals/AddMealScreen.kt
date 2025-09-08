package com.example.presentation.features.main.diary.addMeals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.diary.MealType
import com.example.presentation.common.ui.components.LeftAlignedHeader
import com.example.presentation.common.ui.modifiers.softShadow
import com.example.presentation.common.ui.values.White
import com.example.presentation.extensions.displayName
import com.example.presentation.extensions.icon
import com.example.presentation.features.main.diary.addMeals.addMealBarcode.AddMealBarcodeRoute
import com.example.presentation.features.main.diary.addMeals.addMealsAI.AddMealAIRoute
import com.example.presentation.features.main.diary.addMeals.models.AddMealTab
import java.time.LocalDate

@Composable
fun AddMealRoute(
    mealType: MealType,
    date: LocalDate,
    onBackPressed: () -> Unit,
    onNavigateToAnalyze: (String) -> Unit,
    viewModel: AddMealVM = hiltViewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    AddMealScreen(
        mealType = mealType,
        date = date,
        selectedTab = selectedTab,
        onTabSelected = viewModel::selectTab,
        onBackPressed = onBackPressed,
        onNavigateToAnalyze = onNavigateToAnalyze
    )
}

@Composable
fun AddMealScreen(
    mealType: MealType,
    date: LocalDate,
    selectedTab: AddMealTab,
    onTabSelected: (AddMealTab) -> Unit,
    onBackPressed: () -> Unit,
    onNavigateToAnalyze: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            Column {
                LeftAlignedHeader(
                    mealType = mealType,
                    onNavigateBack = onBackPressed
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    AddMealTab.entries.forEach { tab ->
                        CustomTab(
                            tab = tab,
                            isSelected = selectedTab == tab,
                            onTabSelected = onTabSelected,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0)
    ){ padding ->
        Column {
            when (selectedTab) {
                AddMealTab.BARCODE -> {
                    AddMealBarcodeRoute(
                        modifier = Modifier.padding(padding),
                        mealType = mealType,
                        date = date,
                        onBackPressed = onBackPressed,
                        onNavigateToAnalyze = onNavigateToAnalyze
                    )
                }

                AddMealTab.AI -> {
                    AddMealAIRoute(
                        modifier = Modifier.padding(padding),
                        mealType = mealType,
                        date = date,
                        onBackPressed = onBackPressed,
                        onNavigateToAnalyze = onNavigateToAnalyze
                    )
                }
            }
        }
    }
}

@Composable
fun CustomTab(
    tab: AddMealTab,
    isSelected: Boolean,
    onTabSelected: (AddMealTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ){
            onTabSelected(tab)
        }
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .softShadow(
                    color = MaterialTheme.colorScheme.onBackground.copy(0.08f),
                    blurRadius = 12.dp,
                    offsetX = 0.dp,
                    offsetY = 4.dp,
                    cornerRadius = 16.dp
                )
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.background
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ){
            Icon(
                painter = tab.icon(),
                contentDescription = tab.displayName(),
                modifier = Modifier.size(24.dp),
                tint = if (isSelected)
                    White
                else{
                    if(isSystemInDarkTheme()) White
                    else MaterialTheme.colorScheme.primary
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = tab.displayName(),
            color = if (isSelected)
                MaterialTheme.colorScheme.onBackground
            else
                MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}