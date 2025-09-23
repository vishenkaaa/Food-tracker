package com.example.presentation.features.main.diary.addMeals.addMealsAI.resultAI

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.CloseHeader
import com.example.presentation.common.ui.components.ConfirmationDialog
import com.example.presentation.common.ui.components.CustomButton
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.LeftAlignedHeader
import com.example.presentation.features.main.diary.DiaryVM
import com.example.presentation.features.main.diary.components.CaloriesDisplay
import com.example.presentation.features.main.diary.components.MacroNutrientsBigSection
import com.example.presentation.features.main.diary.components.SwipeDishItem
import com.example.presentation.features.main.diary.editDish.EditDishBottomSheet
import com.example.presentation.features.main.diary.openMeal.models.OpenMealUIState
import kotlinx.coroutines.launch
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
    onTryAgain: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
        onSave = {
            scope.launch {
                val result = viewModel.onSaveDishes(diaryVM)
                if (result.isSuccess) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.saved_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    onBackPressed()
                }
            }
        },
        onTryAgain = onTryAgain
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
    onSave: () -> Unit,
    onTryAgain: () -> Unit
) {
    Box {
        Scaffold(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            topBar = {
                if (uiState.dishes.isEmpty()) CloseHeader(onBackPressed)
                else LeftAlignedHeader(uiState.mealType) { onBackPressed() }
            },
            bottomBar = {
                if(uiState.dishes.isNotEmpty())
                    CustomButton(
                        text = stringResource(R.string.save),
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(bottom = 28.dp)
                    ) { onSave() }
            }
        ) { padding ->
            if (uiState.dishes.isEmpty())
                EmptyStateContent(padding = padding, onTryAgain = onTryAgain)
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding() + 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        CaloriesSection(
                            imgUri = imgUri,
                            calories = uiState.calories,
                        )
                    }

                    item{
                        MacroNutrientsBigSection(
                            protein = uiState.protein,
                            fat = uiState.fat,
                            carbs = uiState.carbs)
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
                enableChangeMealType = false,
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
private fun EmptyStateContent(
    padding: PaddingValues,
    onTryAgain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding()
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.new_profile),
            contentDescription = "image",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 70.dp),
            contentScale = ContentScale.FillWidth
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.oops),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.dish_not_recognized),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.weight(1f))

        CustomButton(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .navigationBarsPadding(),
            text = stringResource(R.string.try_again),
            color = MaterialTheme.colorScheme.error,
            onClick = { onTryAgain() }
        )
    }
}

@Composable
fun CaloriesSection(
    imgUri: String,
    calories: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
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
    }
}