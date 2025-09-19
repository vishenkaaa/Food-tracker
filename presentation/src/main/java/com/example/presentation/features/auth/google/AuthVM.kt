package com.example.presentation.features.auth.google

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.lifecycle.viewModelScope
import com.example.common.ActivityHolder
import com.example.common.GoogleSignInPendingException
import com.example.domain.manager.AuthStateManager
import com.example.domain.model.auth.AuthError
import com.example.domain.usecase.auth.GetGoogleIdTokenUseCase
import com.example.domain.usecase.auth.OldGoogleSignInUseCase
import com.example.domain.usecase.auth.SignInWithGoogleUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.extensions.getLocalizedMessage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthVM @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val getGoogleIdTokenUseCase: GetGoogleIdTokenUseCase,
    private val oldGoogleSignInUseCase: OldGoogleSignInUseCase,
    private val authStateManager: AuthStateManager,
    private val activityHolder: ActivityHolder
) : BaseViewModel() {

    private val _googleSignInIntent = MutableSharedFlow<Intent>()
    val googleSignInIntent = _googleSignInIntent.asSharedFlow()

    fun signInWithGoogle(context: Context, forceNewAccount: Boolean = true) {
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
                                startOldGoogleSignIn(forceNewAccount)
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

    private suspend fun startOldGoogleSignIn(forceNewAccount: Boolean) {
        try {
            oldGoogleSignInUseCase(forceNewAccount)
        } catch (e: GoogleSignInPendingException) {
            _googleSignInIntent.emit(e.intent)
        } catch (e: Exception) {
            handleError(e)
        }
    }

    fun handleOldGoogleSignInResult(result: ActivityResult, context: Context) {
        viewModelScope.launch {
            try {
                handleLoading(true)

                when (result.resultCode) {
                    Activity.RESULT_OK -> {
                        try {
                            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                            val account = task.getResult(ApiException::class.java)
                            val token = account.idToken

                            if (token != null) {
                                processGoogleSignIn(token, context)
                            } else {
                                val errorMessage = context.getString(R.string.error_google_token_null)
                                handleError(Exception(errorMessage))
                            }
                        } catch (apiException: ApiException) {
                            handleError(Exception(context.getString(R.string.error_unknown_auth)))
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                        handleError(Exception(context.getString(R.string.error_user_cancelled_auth)))
                    }
                    else -> {
                        val errorMessage = context.getString(R.string.error_unknown_auth)
                        handleError(Exception("$errorMessage: ${result.resultCode}"))
                    }
                }
            } catch (e: Exception) {
                val localizedMessage = context.getString(R.string.error_unknown_auth)
                handleError(Exception(localizedMessage))
            }
            finally {
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
