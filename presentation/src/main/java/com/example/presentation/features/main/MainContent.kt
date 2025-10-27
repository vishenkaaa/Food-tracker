package com.example.presentation.features.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.domain.manager.UserAuthState
import com.example.presentation.common.ui.values.FoodTrackTheme
import com.example.presentation.features.main.navigation.AppNavHost
import com.example.presentation.features.main.navigation.AppNavigationBar

@Composable
fun MainContent(
    navController: NavHostController,
    userAuthState: UserAuthState,
    shouldShowBottomBar: Boolean,
    onDestinationChanged: (String?) -> Unit,
    onBackPressed: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            onDestinationChanged(destination.route)
        }
    }

    BackHandler {
        onBackPressed()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(
                top = 0.dp,
                bottom = if (shouldShowBottomBar)
                    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                else 0.dp
            ),
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    AppNavHost(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        userAuthState = userAuthState
                    )

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    AnimatedVisibility(
                        visible = shouldShowBottomBar,
                        modifier = Modifier.align(Alignment.BottomCenter),
                        enter = slideInVertically(
                            initialOffsetY = { it }
                        ) + fadeIn(animationSpec = tween(durationMillis = 300)),

                        exit = slideOutVertically(
                            targetOffsetY = { it }
                        ) + fadeOut(animationSpec = tween(durationMillis = 300))
                    ) {
                        AppNavigationBar(currentDestination, navController)
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun MainContentPreview(){
    val fakeNavController = rememberNavController()
    val fakeUserAuthState = UserAuthState(
        isLoading = false,
        isLoggedIn = true,
        isFullyRegistered = true
    )

    FoodTrackTheme {
        MainContent(
            fakeNavController, fakeUserAuthState, true, {}, {}
        )
    }
}