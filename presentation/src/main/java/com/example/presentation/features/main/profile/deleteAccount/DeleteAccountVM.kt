package com.example.presentation.features.main.profile.deleteAccount

import android.app.Activity
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.common.ActivityHolder
import com.example.domain.manager.AuthStateManager
import com.example.domain.model.auth.AuthError
import com.example.domain.usecase.auth.DeleteAccountUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.extensions.getLocalizedMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAccountVM @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val authStateManager: AuthStateManager,
    private val activityHolder: ActivityHolder
) : BaseViewModel() {
    private val _showToast = MutableSharedFlow<Unit>()
    val showToast = _showToast.asSharedFlow()

    fun onDelete(context: Context) {
        try {
            if (context is Activity)
                activityHolder.setActivity(context)

            viewModelScope.launch {
                handleLoading(true)
                val deleteResult = deleteAccountUseCase()
                deleteResult.fold(
                    onSuccess = {
                        authStateManager.setAuthState(isLoggedIn = false, isFullyRegistered = false)
                        _showToast.emit(Unit)
                        handleLoading(false)
                    },
                    onFailure = { error ->
                        val localizedMessage =
                            if (error is AuthError) error.getLocalizedMessage(context)
                            else context.getString(R.string.error_unknown_auth)
                        handleError(Exception(localizedMessage), context)
                    }
                )
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    fun consumeError() {
        clearErrors()
    }
}