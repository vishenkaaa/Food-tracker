package com.example.presentation.features.auth.google

import android.app.Activity
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.common.ActivityHolder
import com.example.domain.manager.AuthStateManager
import com.example.domain.model.auth.AuthError
import com.example.domain.usecase.auth.GetGoogleIdTokenUseCase
import com.example.domain.usecase.auth.SignInWithGoogleUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.extensions.getLocalizedMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthVM @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val getGoogleIdTokenUseCase: GetGoogleIdTokenUseCase,
    private val authStateManager: AuthStateManager,
    private val activityHolder: ActivityHolder
) : BaseViewModel() {

    fun signInWithGoogle(context: Context, forceNewAccount: Boolean = false) {
        viewModelScope.launch {
            clearErrors()
            handleLoading(true)

            try {
                if (context is Activity)
                    activityHolder.setActivity(context)

                val idTokenResult = getGoogleIdTokenUseCase(forceNewAccount)

                idTokenResult.fold(
                    onSuccess = { idToken ->
                        processGoogleSignIn(idToken, context)
                    },
                    onFailure = { error ->
                        if(!hasInternet(context)) {
                            handleError(
                                Exception(context.getString(R.string.no_internet_connection)),
                                context
                            )
                            return@launch
                        }
                        when {
                            error is AuthError.NoCredentialAvailable && context is Activity -> {
                                val localizedMessage = context.getString(R.string.error_no_credential_available)
                                handleError(Exception(localizedMessage), context)
                                return@launch
                            }
                            else -> {
                                val localizedMessage = if (error is AuthError) error.getLocalizedMessage(context)
                                else context.getString(R.string.error_unknown_auth)

                                handleError(Exception(localizedMessage), context)
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                val localizedMessage = context.getString(R.string.error_unknown_auth)
                handleError(Exception(localizedMessage))
            } finally {
                handleLoading(false)
            }
        }
    }

    private suspend fun processGoogleSignIn(idToken: String, context: Context) {
        try {
            val result = signInWithGoogleUseCase(idToken)
            result.fold(
                onSuccess = { user ->
                    authStateManager.setAuthState(true, user.targetCalories != 0)
                },
                onFailure = { error ->
                    val localizedMessage = if (error is AuthError)
                        error.getLocalizedMessage(context)
                    else
                        context.getString(R.string.error_unknown_auth)
                    handleError(Exception(localizedMessage))
                }
            )
        } catch (e: Exception) {
            val localizedMessage = context.getString(R.string.error_unknown_auth)
            handleError(Exception(localizedMessage))
        }
    }
}
