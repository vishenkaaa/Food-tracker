package com.example.presentation.features.main.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.example.presentation.features.auth.google.AuthRoute
import com.example.presentation.features.auth.onboarding.OnboardingRoute
import com.example.presentation.features.main.diary.DiaryRoute
import com.example.presentation.features.main.diary.DiaryVM
import com.example.presentation.features.main.diary.addMeals.addMealsAI.AddMealAIRoute
import com.example.presentation.features.main.diary.addMeals.addMealsAI.dishLoading.DishLoadingRoute
import com.example.presentation.features.main.diary.addMeals.addMealsAI.resultAI.ResultAIRoute
import com.example.presentation.features.main.diary.openMeal.OpenMealRoute
import com.example.presentation.features.main.idle.IdleRoute
import com.example.presentation.features.main.profile.ProfileRoute
import com.example.presentation.features.main.profile.about.AboutRoute
import com.example.presentation.features.main.profile.deleteAccount.DeleteAccountRoute
import com.example.presentation.features.main.statistics.StatisticsRoute
import com.example.presentation.model.DishDto
import com.example.presentation.model.toDomain
import com.example.presentation.model.toDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Composable
fun AppNavHost(
    modifier: Modifier,
    navController: NavHostController,
    userAuthState: UserAuthState
) {
    LaunchedEffect(userAuthState.isLoggedIn, userAuthState.isFullyRegistered) {
        delay(1000)
        if (!userAuthState.isLoading) {
            withContext(Dispatchers.Main) {
                when {
                    userAuthState.isLoggedIn == null || userAuthState.isLoggedIn == false -> {
                        navController.navigate(Graphs.Login) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }

                    !userAuthState.isFullyRegistered -> {
                        navController.navigate(LoginGraph.Onboarding) {
                            popUpTo(Graphs.IdleScreen) { inclusive = true }
                        }
                    }

                    else -> {
                        navController.navigate(MainGraph.Dairy) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }

    NavHost(
        modifier = modifier
            .fillMaxSize(),
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
    val DISHES_KEY = "dishes"

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
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        DISHES_KEY,
                        dishes.toDto()
                    )

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
            val dishesDto = navController.previousBackStackEntry?.savedStateHandle?.get<List<DishDto>>(DISHES_KEY) ?: listOf()
            val dishes = dishesDto.toDomain()

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Graphs.Main>()
            }
            val diaryVM = hiltViewModel<DiaryVM>(parentEntry)

            OpenMealRoute(
                diaryVM = diaryVM,
                mealType = args.mealType,
                dishes = dishes,
                date = LocalDate.parse(args.date),
                targetCalories = args.targetCalories,
                onBackPressed = { navController.popBackStack() },
                onNavigateToAddDish = { mealType, date ->
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
                onNavigateToAnalyze = { imgUri ->
                    navController.navigate(
                        MainGraph.DishLoading(
                            mealType = args.mealType,
                            date = args.date,
                            imgUri = imgUri
                        )
                    ) {
                        popUpTo<MainGraph.AddMealAI> {
                            inclusive = true
                        }
                    }
                },
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable<MainGraph.DishLoading> { backStackEntry ->
            val args = backStackEntry.toRoute<MainGraph.DishLoading>()
            DishLoadingRoute(
                imgUri = args.imgUri,
                onBackPressed = { navController.popBackStack() },
                onNavigateToResults = { dishes ->
                    val parentEntry = navController.getBackStackEntry<Graphs.Main>()
                    parentEntry.savedStateHandle[DISHES_KEY] = dishes.toDto()

                    navController.navigate(
                        MainGraph.ResultAI(
                            mealType = args.mealType,
                            date = args.date,
                            imgUri = args.imgUri
                        )
                    ) {
                        popUpTo<MainGraph.DishLoading> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<MainGraph.ResultAI> { backStackEntry ->
            val args = backStackEntry.toRoute<MainGraph.ResultAI>()

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Graphs.Main>()
            }
            val dishesDto = parentEntry.savedStateHandle.get<List<DishDto>>(DISHES_KEY) ?: listOf()
            val dishes = dishesDto.toDomain()
            val diaryVM = hiltViewModel<DiaryVM>(parentEntry)

            ResultAIRoute(
                diaryVM = diaryVM,
                mealType = args.mealType,
                dishes = dishes,
                date = LocalDate.parse(args.date),
                onBackPressed = {
                    navController.navigate(MainGraph.Dairy) {
                        popUpTo<Graphs.Main> { inclusive = false }
                        launchSingleTop = true
                    }
                },
                imgUri = args.imgUri,
                onTryAgain = {
                    navController.navigate(
                        MainGraph.DishLoading(
                            mealType = args.mealType,
                            date = args.date,
                            imgUri = args.imgUri
                        )
                    ) {
                        popUpTo<MainGraph.AddMealAI> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<MainGraph.Statistics> {
            StatisticsRoute()
        }

        composable<MainGraph.Profile> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Graphs.Main>()
            }
            ProfileRoute(
                viewModel = hiltViewModel(parentEntry),
                onDeleteAccount = { navController.navigate(MainGraph.DeleteAccount) },
                onAbout = { navController.navigate(MainGraph.About) }
            )
        }

        composable<MainGraph.DeleteAccount> {
            DeleteAccountRoute{ navController.popBackStack() }
        }

        composable<MainGraph.About> {
            AboutRoute{ navController.popBackStack() }
        }
    }
}