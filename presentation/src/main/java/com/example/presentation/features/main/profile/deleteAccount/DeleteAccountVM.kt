package com.example.presentation.features.main.profile.deleteAccount

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.common.ActivityHolder
import com.example.domain.manager.AuthStateManager
import com.example.domain.model.auth.AuthError
import com.example.domain.usecase.auth.DeleteAccountUseCase
import com.example.domain.usecase.auth.GetCurrentUserEmailUseCase
import com.example.domain.usecase.auth.GetGoogleIdTokenUseCase
import com.example.domain.usecase.auth.SignInWithGoogleUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.common.utils.WidgetEventNotifier
import com.example.presentation.extensions.getLocalizedMessage
import com.example.presentation.widget.CaloriesSmallWidgetReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class DeleteAccountVM @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val authStateManager: AuthStateManager,
    private val activityHolder: ActivityHolder,
    private val getGoogleIdTokenUseCase: GetGoogleIdTokenUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val getCurrentUserEmailUseCase: GetCurrentUserEmailUseCase
) : BaseViewModel() {
    private val _showToast = MutableSharedFlow<Unit>()
    val showToast = _showToast.asSharedFlow()

    private val _showReauthDialog = MutableStateFlow(false)
    val showReauthDialog = _showReauthDialog.asStateFlow()

    fun onCancelDialog() {
        _showReauthDialog.value = false
    }

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

                        WidgetEventNotifier.notifyAuthChanged(context)

                        handleLoading(false)
                    },
                    onFailure = { error ->
                        handleLoading(false)
                        when (error) {
                            is AuthError.ReauthenticationRequired -> {
                                _showReauthDialog.value = true
                            }
                            is AuthError -> {
                                val localizedMessage = error.getLocalizedMessage(context)
                                handleError(Exception(localizedMessage), context)
                            }
                            else -> {
                                val localizedMessage = context.getString(R.string.error_account_deletion)
                                handleError(Exception(localizedMessage), context)
                            }
                        }
                    }
                )
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    fun reauthenticateAndDelete(context: Context) {
        viewModelScope.launch {
            clearErrors()
            handleLoading(true)
            _showReauthDialog.value = false

            try {
                if (context is Activity)
                    activityHolder.setActivity(context)

                val currentUserEmail = getCurrentUserEmailUseCase()

                if (currentUserEmail == null) {
                    handleLoading(false)
                    handleError(Exception(context.getString(R.string.user_not_authenticated)), context)
                    return@launch
                }

                val idTokenResult = getGoogleIdTokenUseCase(forceNewAccount = true)

                idTokenResult.fold(
                    onSuccess = { idToken ->
                        val tokenEmail = parseEmailFromIdToken(idToken)

                        if (tokenEmail != currentUserEmail) {
                            handleLoading(false)
                            handleError(
                                Exception(context.getString(R.string.error_different_account)),
                                context
                            )
                            return@fold
                        }

                        val signInResult = signInWithGoogleUseCase(idToken)
                        signInResult.fold(
                            onSuccess = {
                                onDelete(context)
                            },
                            onFailure = { error ->
                                handleLoading(false)
                                val localizedMessage = if (error is AuthError)
                                    error.getLocalizedMessage(context)
                                else
                                    context.getString(R.string.error_unknown_auth)
                                handleError(Exception(localizedMessage), context)
                            }
                        )
                    },
                    onFailure = { error ->
                        handleLoading(false)
                        val localizedMessage = if (error is AuthError)
                            error.getLocalizedMessage(context)
                        else
                            context.getString(R.string.error_unknown_auth)
                        handleError(Exception(localizedMessage), context)
                    }
                )
            } catch (e: Exception) {
                handleLoading(false)
                val localizedMessage = context.getString(R.string.error_unknown_auth)
                handleError(Exception(localizedMessage))
            }
        }
    }

    private fun parseEmailFromIdToken(idToken: String): String? {
        return try {
            val parts = idToken.split(".")
            if (parts.size != 3) return null

            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val decodedString = String(decodedBytes, Charsets.UTF_8)

            val jsonObject = JSONObject(decodedString)
            jsonObject.optString("email", null.toString())
        } catch (e: Exception) {
            Log.e("DeleteAccountVM", "Error parsing ID token", e)
            null
        }
    }

    fun consumeError() {
        clearErrors()
    }
}