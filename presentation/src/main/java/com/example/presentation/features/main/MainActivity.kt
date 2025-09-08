package com.example.presentation.features.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.domain.manager.UserAuthState
import com.example.presentation.R
import com.example.presentation.common.ui.values.FoodTrackTheme
import com.example.presentation.features.MainContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainVM by viewModels()
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        viewModel.checkUserState()
        setupSplashScreen(splashScreen)
        setContent()
        observeUiEvents()
    }

    private fun observeUiEvents() {
        lifecycleScope.launch {
            viewModel.uiEvents.collect { event ->
                when (event) {
                    is UiEvent.ExitApp -> finish()
                    is UiEvent.ShowExitMessage -> {
                        Toast.makeText(this@MainActivity, getString(R.string.all_exit), Toast.LENGTH_SHORT).show()
                    }
                    is UiEvent.NavigateBack -> {
                        navController?.popBackStack()
                    }
                }
            }
        }
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
            val userAuthState = viewModel.userAuthState.collectAsStateWithLifecycle()
            val shouldShowBottomBar by viewModel.shouldShowBottomBar.collectAsStateWithLifecycle()
            navController = rememberNavController()

            FoodTrackTheme {
                MainContent(
                    navController = navController!!,
                    userAuthState = userAuthState.value,
                    shouldShowBottomBar = shouldShowBottomBar,
                    onDestinationChanged = viewModel::onDestinationChanged,
                    onBackPressed = viewModel::onBackPressed
                )
            }
        }
    }
}