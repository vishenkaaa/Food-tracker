package com.example.presentation.features.main

import androidx.lifecycle.viewModelScope
import com.example.domain.manager.AuthStateManager
import com.example.domain.manager.UserAuthState
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.features.main.navigation.Graphs
import com.example.presentation.features.main.navigation.MainGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val authStateManager: AuthStateManager
) : BaseViewModel() {

    val userAuthState: StateFlow<UserAuthState> = authStateManager.userAuthState

    fun checkUserState() {
        viewModelScope.launch {
            authStateManager.checkAndUpdateUserState()
        }
    }

    fun isDestinationInMainGraph(route: String?): Boolean {
        val mainGraphRoutes = listOf(
            MainGraph.Statistics::class.qualifiedName,
            MainGraph.Dairy::class.qualifiedName,
            MainGraph.Profile::class.qualifiedName,
            Graphs.Main::class.qualifiedName
        )
        return route in mainGraphRoutes
    }
}