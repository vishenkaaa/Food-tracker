package com.example.presentation.features.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.domain.manager.UserAuthState
import com.example.presentation.R
import com.example.presentation.common.ui.values.FoodTrackTheme
import com.example.presentation.features.main.navigation.AppNavHost
import com.example.presentation.features.main.navigation.AppNavigationBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainVM by viewModels()
    private var onBackPressClickTime: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        viewModel.checkUserState()
        setupSplashScreen(splashScreen)
        setContent()
    }

    private fun setupSplashScreen(splashScreen: SplashScreen) {
        var userAuthState: UserAuthState by mutableStateOf(UserAuthState(isLoading = true))
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userAuthState.onEach { userAuthState = it }.collect {}
            }
        }
        splashScreen.setKeepOnScreenCondition {
            userAuthState.isLoading
        }
    }

    private fun setContent(){
        setContent{
            val snackbarHostState = remember { SnackbarHostState() }
            var shouldShowBottomBar by remember { mutableStateOf(false) }
            val navController = rememberNavController()
                .apply {
                    addOnDestinationChangedListener { _, destination, _ ->
                        shouldShowBottomBar = viewModel.isDestinationInMainGraph(destination.route)
                    }
                }

            val userAuthState = viewModel.userAuthState.collectAsStateWithLifecycle()

            BackHandler(
                enabled = true,
                onBack = { handleBackPress(navController) }
            )

            FoodTrackTheme {
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
                                    userAuthState = userAuthState.value,
                                    shouldShowBottomBar = shouldShowBottomBar,
                                )

                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination

                                AnimatedVisibility(
                                    visible = shouldShowBottomBar,
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                ) {
                                    AppNavigationBar(currentDestination, navController)
                                }
                            }
                        },
                    )
                }
            }
        }
    }

    private fun handleBackPress(navController: NavHostController) {
        if (navController.currentDestination?.route == navController.graph.startDestinationRoute) {
            val currentTime = System.currentTimeMillis()
            val interval = currentTime - (onBackPressClickTime ?: 0)

            if (interval < 2000) {
                finish()
            } else {
                onBackPressClickTime = currentTime
                Toast.makeText(this, getString(R.string.all_exit), Toast.LENGTH_SHORT).show()
            }
        } else {
            navController.popBackStack()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    FoodTrackTheme {

    }
}