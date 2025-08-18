package com.example.presentation.features.main.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.example.domain.manager.UserAuthState
import com.example.domain.model.diary.Dish
import com.example.presentation.features.auth.google.AuthRoute
import com.example.presentation.features.auth.onboarding.OnboardingRoute
import com.example.presentation.features.main.profile.deleteAccount.DeleteAccountRoute
import com.example.presentation.features.main.diary.DiaryRoute
import com.example.presentation.features.main.diary.addMeals.cameraAI.AddMealAIRoute
import com.example.presentation.features.main.diary.openMeal.OpenMealRoute
import com.example.presentation.features.main.idle.IdleRoute
import com.example.presentation.features.main.profile.ProfileRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Composable
fun AppNavHost(
    modifier: Modifier,
    navController: NavHostController,
    userAuthState: UserAuthState,
    shouldShowBottomBar: Boolean,
) {
    LaunchedEffect(userAuthState.isLoggedIn, userAuthState.isFullyRegistered) {
        delay(1000)
        if (!userAuthState.isLoading) {
            withContext(Dispatchers.Main) {
                when {
                    userAuthState.isLoggedIn == null || userAuthState.isLoggedIn == false -> {
                        navController.navigate(Graphs.Login) {
                            popUpTo(Graphs.IdleScreen) { inclusive = true }
                        }
                    }

                    !userAuthState.isFullyRegistered -> {
                        navController.navigate(LoginGraph.Onboarding) {
                            popUpTo(Graphs.IdleScreen) { inclusive = true }
                        }
                    }

                    else -> {
                        navController.navigate(MainGraph.Dairy) {
                            popUpTo(Graphs.IdleScreen) { inclusive = true }
                        }
                    }
                }
            }
        }
    }

    NavHost(
        modifier = modifier
            .fillMaxSize(),
//            .padding(bottom = if (shouldShowBottomBar) 32.dp else 0.dp),
        navController = navController,
        startDestination = Graphs.IdleScreen
    ) {
        composable<Graphs.IdleScreen> { IdleRoute() }
        loginGraph(navController)
        mainGraph(navController)
    }
}

private fun NavGraphBuilder.loginGraph(
    navController: NavController,
) {
    navigation<Graphs.Login>(
        startDestination = LoginGraph.Google,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(500)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(500)
            )
        }
    ) {
        composable<LoginGraph.Google>(
            enterTransition = {
                return@composable fadeIn()
            },
            exitTransition = {
                return@composable fadeOut()
            }
        ) {
            AuthRoute()
        }

        composable<LoginGraph.Onboarding> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Graphs.Login>()
            }
            OnboardingRoute(
                viewModel = hiltViewModel(parentEntry)
            )
        }
    }
}

private fun NavGraphBuilder.mainGraph(
    navController: NavController
) {
    navigation<Graphs.Main>(
        startDestination = MainGraph.Dairy,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable<MainGraph.Dairy> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Graphs.Main>()
            }
            DiaryRoute(
                viewModel = hiltViewModel(parentEntry),
                onNavigateToOpenMeal = { mealType, dishes, date, targetCalories ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("dishes", dishes)

                    navController.navigate(
                        MainGraph.OpenMeal(
                            mealType = mealType,
                            date = date.toString(),
                            targetCalories = targetCalories
                        )
                    )
                },
                onNavigateToAddMeal = { mealType, date ->
                    navController.navigate(
                        MainGraph.AddMealAI(
                            mealType = mealType,
                            date = date.toString(),
                        )
                    )
                }
            )
        }

        composable<MainGraph.AddMealAI> { backStackEntry ->
            val args = backStackEntry.toRoute<MainGraph.AddMealAI>()
            AddMealAIRoute(
                mealType = args.mealType,
                date = LocalDate.parse(args.date),
            )
        }

        composable<MainGraph.OpenMeal>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            },
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<MainGraph.OpenMeal>()
            val dishes = navController.previousBackStackEntry?.savedStateHandle?.get<List<Dish>>("dishes") ?: listOf()
            OpenMealRoute(
                mealType = args.mealType,
                dishes = dishes,
                date = LocalDate.parse(args.date),
                targetCalories = args.targetCalories,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddDish = { mealType, date ->
                    // TODO Navigate to add dish screen
                }
            )
        }

        composable<MainGraph.Statistics> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Statistics Screen")
            }
        }

        composable<MainGraph.Profile> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Graphs.Main>()
            }
            ProfileRoute(
                viewModel = hiltViewModel(parentEntry),
                onDeleteAccount = { navController.navigate(MainGraph.DeleteAccount) }
            )
        }

        composable<MainGraph.DeleteAccount> {
            DeleteAccountRoute{ navController.popBackStack() }
        }
    }
}