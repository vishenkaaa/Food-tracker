package com.example.presentation.features.main.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.domain.manager.UserAuthState
import com.example.presentation.features.main.idle.IdleRoute
import com.example.presentation.features.auth.google.AuthRoute
import com.example.presentation.features.auth.onboarding.OnboardingRoute
import com.example.presentation.features.main.diary.DiaryRoute

@Composable
fun AppNavHost(
    modifier: Modifier,
    navController: NavHostController,
    userAuthState: UserAuthState,
    shouldShowBottomBar: Boolean,
) {
    LaunchedEffect(userAuthState.isLoggedIn, userAuthState.isFullyRegistered) {
        if (!userAuthState.isLoading) {
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

    NavHost(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = if (shouldShowBottomBar) 32.dp else 0.dp),
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
        composable<MainGraph.Dairy> {
           DiaryRoute()
        }
    }
}
