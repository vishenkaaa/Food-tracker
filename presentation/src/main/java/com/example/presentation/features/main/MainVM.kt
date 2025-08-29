package com.example.presentation.features.main

import androidx.lifecycle.viewModelScope
import com.example.domain.manager.AuthStateManager
import com.example.domain.manager.UserAuthState
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.features.main.navigation.Graphs
import com.example.presentation.features.main.navigation.LoginGraph
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
        if (route == null) return false

        return route == MainGraph.Dairy::class.qualifiedName ||
                route == MainGraph.Statistics::class.qualifiedName ||
                route == MainGraph.Profile::class.qualifiedName
    }

    fun isDestinationRoot(route: String?): Boolean {
        if (route == null) return false

        return route == MainGraph.Dairy::class.qualifiedName ||
                route == MainGraph.Statistics::class.qualifiedName ||
                route == MainGraph.Profile::class.qualifiedName ||
                route == LoginGraph.Google::class.qualifiedName
    }
}