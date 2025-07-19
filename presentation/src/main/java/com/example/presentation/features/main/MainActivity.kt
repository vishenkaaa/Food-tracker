package com.example.presentation.features.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.example.foodtrack.ui.theme.FoodTrackTheme
import com.example.presentation.R
import com.example.presentation.features.auth.AuthRoute
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainVM by viewModels()
    private var onBackPressClickTime: Long? = null
    private var navHostController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
            val navController = navHostController ?: return@addCallback

            if (navController.currentDestination?.route == navController.graph.startDestinationRoute) {
                val currentTime = System.currentTimeMillis()
                val interval = currentTime - (onBackPressClickTime ?: 0)
                if (interval < 2000) {
                    finish()
                } else {
                    onBackPressClickTime = currentTime
                    Toast.makeText(this@MainActivity, getString(R.string.all_exit), Toast.LENGTH_SHORT).show()
                }
            } else {
                navController.popBackStack()
            }
        }

        setupSplashScreen(splashScreen)
        setContent()
    }

    private fun setupSplashScreen(splashScreen: SplashScreen) {
        var userAuth: UserAuthState by mutableStateOf(UserAuthState(isLoading = true))
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userAuthState.onEach { userAuth = it }.collect {}
            }
        }
        splashScreen.setKeepOnScreenCondition {
            userAuth.isLoading
        }
    }

    private fun setContent(){
        setContent{
            FoodTrackTheme {
                AuthRoute()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    FoodTrackTheme {

    }
}