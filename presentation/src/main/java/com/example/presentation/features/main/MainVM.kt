package com.example.presentation.features.main

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.domain.manager.AuthStateManager
import com.example.domain.manager.UserAuthState
import com.example.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.domain.usecase.meal.GetCaloriesProgressUseCase
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.features.main.navigation.LoginGraph
import com.example.presentation.features.main.navigation.MainGraph
import com.example.presentation.widget.WidgetUpdater
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val authStateManager: AuthStateManager,
    private val widgetUpdater: WidgetUpdater,
    private val getCaloriesUseCase: GetCaloriesProgressUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : BaseViewModel() {

    val userAuthState: StateFlow<UserAuthState> = authStateManager.userAuthState

    private val _shouldShowBottomBar = MutableStateFlow(false)
    val shouldShowBottomBar: StateFlow<Boolean> = _shouldShowBottomBar.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    private var lastBackPressTime: Long = 0
    private var currentRoute: String? = null

    init {
        observeAuthState()
    }

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

    private fun observeAuthState() {
        viewModelScope.launch {
            userAuthState.collect { authState ->
                if (!authState.isLoading && authState.isLoggedIn == true) {
                    // Користувач авторизований - можемо оновити віджет
                    // Але потрібен Context, тому створимо метод для MainActivity
                }
            }
        }
    }

    fun onUserAuthenticated(context: Context) {
        viewModelScope.launch {
            updateWidgetIfNeeded(context)
        }
    }

    fun updateWidgetData(context: Context) {
        viewModelScope.launch {
            updateWidgetIfNeeded(context)
        }
    }

    private suspend fun updateWidgetIfNeeded(context: Context) {
        try {
            if (!widgetUpdater.hasCaloriesWidget(context)) {
                // Віджет не встановлений - не робимо зайву роботу
                return
            }

            val userId = getCurrentUserIdUseCase() ?: return
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val caloriesResult = getCaloriesUseCase(userId, currentDate)
            caloriesResult.fold(
                onSuccess = { caloriesProgress ->
                    widgetUpdater.updateCaloriesWidget(context, caloriesProgress)
                },
                onFailure = { error ->
                    error.printStackTrace()
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
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