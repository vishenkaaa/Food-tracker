package com.example.presentation.features.auth.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.domain.model.Gender
import com.example.domain.model.Goal
import com.example.domain.model.UserActivityLevel
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.CustomButton
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.LoadingBackground
import com.example.presentation.features.auth.onboarding.OnboardingVM.Companion.MAX_STEPS
import com.example.presentation.features.auth.onboarding.OnboardingVM.Companion.WELCOME_STEP
import com.example.presentation.features.auth.onboarding.components.BirthDateStep
import com.example.presentation.features.auth.onboarding.components.CurrentWeightStep
import com.example.presentation.features.auth.onboarding.components.GenderSelectionStep
import com.example.presentation.features.auth.onboarding.components.GoalSelectionStep
import com.example.presentation.features.auth.onboarding.components.HeightStep
import com.example.presentation.features.auth.onboarding.components.ResultStep
import com.example.presentation.features.auth.onboarding.components.UserActivityLevelSectionStep
import com.example.presentation.features.auth.onboarding.components.WeightChangeStep
import com.example.presentation.features.auth.onboarding.components.WelcomeStep
import java.time.LocalDate

@Composable
fun OnboardingRoute(
    viewModel: OnboardingVM = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val baseUiState by viewModel.baseUiState.collectAsState()

    OnboardingScreen(
        baseUiState = baseUiState,
        uiState = uiState,
        onGoalSelected = viewModel::onGoalSelected,
        onWeightChangeSelected = viewModel::onWeightChangeSelected,
        onGenderSelected = viewModel::onGenderSelected,
        onActivityLevelSelected = viewModel::onActivityLevelSelected,
        onCurrentWeightSelected = viewModel::onCurrentWeightSelected,
        onHeightSelected = viewModel::onHeightSelected,
        onBirthDateSelected = viewModel::onBirthDateSelected,
        onSave = { viewModel.saveUserInfo(context) },
        onBackPressed = { viewModel.onBackPressed() },
        onNextStep = { viewModel.onNextStep(context) },
        onErrorConsume = { viewModel.consumeError() },
        onFinish = { viewModel.onFinish() }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnboardingScreen(
    baseUiState: BaseUiState,
    uiState: TargetUiState,
    onGoalSelected: (Goal) -> Unit,
    onWeightChangeSelected: (Float) -> Unit,
    onGenderSelected: (Gender) -> Unit,
    onActivityLevelSelected: (UserActivityLevel) -> Unit,
    onCurrentWeightSelected: (Float) -> Unit,
    onHeightSelected: (Int) -> Unit,
    onBirthDateSelected: (LocalDate) -> Unit,
    onSave: () -> Unit,
    onBackPressed: () -> Unit,
    onNextStep: () -> Unit,
    onErrorConsume: () -> Unit,
    onFinish: () -> Unit
) {

    val focusManager = LocalFocusManager.current

    BackHandler {
        if (uiState.step != MAX_STEPS) onBackPressed()
    }

    val pagerState = rememberPagerState(
        initialPage = uiState.step,
        pageCount = { maxOf(uiState.totalSteps + 1, 9) }
    )

    LaunchedEffect(uiState.step) {
        pagerState.animateScrollToPage(uiState.step)
    }

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(0.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                navigationIcon = {
                    if (uiState.step != MAX_STEPS)
                        IconButton(
                            onClick = { onBackPressed() },
                            modifier = Modifier
                                .padding(end = 16.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.back),
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                },
                title = {
                    Text(buildAnnotatedString {
                        withStyle(
                            style = MaterialTheme.typography.displaySmall.toSpanStyle().copy(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        ) { append(stringResource(R.string.food)) }
                        withStyle(
                            style = MaterialTheme.typography.displaySmall.toSpanStyle().copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) { append(stringResource(R.string.snap)) }
                    })
                }
            )

            if (uiState.step > 0) {
                LinearWavyProgressIndicator(
                    progress = {
                        val adjustedStep =
                            if (uiState.goal == Goal.MAINTAIN && uiState.step > 2) uiState.step - 1
                            else uiState.step
                        val currentProgressStep = (adjustedStep - 1).coerceAtLeast(0)
                        currentProgressStep.toFloat() / (uiState.totalSteps).toFloat()
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outline,
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                userScrollEnabled = false,
                pageSpacing = 0.dp
            ) {
                when (uiState.step) {
                    0 -> WelcomeStep()
                    1 -> GoalSelectionStep(uiState.goal, onGoalSelected)
                    2 -> WeightChangeStep(
                        uiState.goal ?: Goal.MAINTAIN,
                        uiState.weightChange,
                        onWeightChangeSelected
                    )
                    3 -> CurrentWeightStep(uiState.currentWeight, onCurrentWeightSelected)
                    4 -> HeightStep(uiState.height, onHeightSelected, onNextStep)
                    5 -> GenderSelectionStep(uiState.gender, onGenderSelected)
                    6 -> UserActivityLevelSectionStep(
                        uiState.activityLevel,
                        onActivityLevelSelected
                    )
                    7 -> BirthDateStep(uiState.birthDate, onBirthDateSelected)
                    8 -> ResultStep(uiState.macroNutrients, uiState.bmi, uiState.targetCalories)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            CustomButton(
                modifier = Modifier
                    .imePadding()
                    .padding(bottom = 80.dp),
                text = when (uiState.step){
                    WELCOME_STEP -> stringResource(R.string.start)
                    MAX_STEPS -> stringResource(R.string.finish)
                    else -> stringResource(R.string.continue_)
                },
                icon = if (uiState.step == 0) painterResource(R.drawable.arrow_start) else null,
                iconPositionStart = false,
                onClick = {
                    focusManager.clearFocus()
                    if (uiState.step != MAX_STEPS) onNextStep() else onFinish()
                },
                enabled = uiState.isNextEnabled
            )
        }

        LoadingBackground(baseUiState.isLoading)

        HandleError(
            baseUiState = baseUiState,
            onErrorConsume = onErrorConsume,
            onConnectionRetry = onSave
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TargetScreenPreview() {
    OnboardingScreen(
        BaseUiState(),
        TargetUiState(),
        {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}
    )
}