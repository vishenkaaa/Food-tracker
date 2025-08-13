package com.example.presentation.features.main.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.example.domain.manager.UserAuthState
import com.example.presentation.features.auth.google.AuthRoute
import com.example.presentation.features.auth.onboarding.OnboardingRoute
import com.example.presentation.features.main.deleteAccount.DeleteAccountRoute
import com.example.presentation.features.main.diary.DiaryRoute
import com.example.presentation.features.main.idle.IdleRoute
import com.example.presentation.features.main.profile.ProfileRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

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
        composable<MainGraph.Dairy> {
            DiaryRoute()
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