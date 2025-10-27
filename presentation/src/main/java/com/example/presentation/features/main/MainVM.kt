package com.example.presentation.features.main

import androidx.lifecycle.viewModelScope
import com.example.domain.manager.AuthStateManager
import com.example.domain.manager.UserAuthState
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.features.main.navigation.LoginGraph
import com.example.presentation.features.main.navigation.MainGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val authStateManager: AuthStateManager
) : BaseViewModel() {

    val userAuthState: StateFlow<UserAuthState> = authStateManager.userAuthState

    private val _shouldShowBottomBar = MutableStateFlow(false)
    val shouldShowBottomBar: StateFlow<Boolean> = _shouldShowBottomBar.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    private var lastBackPressTime: Long = 0
    private var currentRoute: String? = null

    fun onBackPressed() {
        viewModelScope.launch {
            if (isDestinationRoot(currentRoute)) {
                val currentTime = System.currentTimeMillis()
                val timeDiff = currentTime - lastBackPressTime

                if (timeDiff < 2000) {
                    _uiEvents.emit(UiEvent.ExitApp)
                } else {
                    lastBackPressTime = currentTime
                    _uiEvents.emit(UiEvent.ShowExitMessage)
                }
            } else {
                _uiEvents.emit(UiEvent.NavigateBack)
            }
        }
    }

    fun resetLoadingState() {
        viewModelScope.launch {
            authStateManager.resetToLoading()
        }
    }

    fun checkUserState() {
        viewModelScope.launch {
            authStateManager.checkAndUpdateUserState()
        }
    }

    fun onDestinationChanged(route: String?) {
        currentRoute = route
        _shouldShowBottomBar.value = isDestinationInMainGraph(route)
    }

    private fun isDestinationInMainGraph(route: String?): Boolean {
        if (route == null) return false

        return route == MainGraph.Dairy::class.qualifiedName ||
                route == MainGraph.Statistics::class.qualifiedName ||
                route == MainGraph.Profile::class.qualifiedName
    }

    private fun isDestinationRoot(route: String?): Boolean {
        if (route == null) return false

        return route == MainGraph.Dairy::class.qualifiedName ||
                route == MainGraph.Statistics::class.qualifiedName ||
                route == MainGraph.Profile::class.qualifiedName ||
                route == LoginGraph.Google::class.qualifiedName
    }
}

sealed class UiEvent {
    data object ExitApp : UiEvent()
    data object ShowExitMessage : UiEvent()
    data object NavigateBack : UiEvent()
}