package com.example.presentation.arch

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

open class BaseViewModel : ViewModel() {

    private val _baseUiState = MutableStateFlow(BaseUiState())
    val baseUiState: StateFlow<BaseUiState> = _baseUiState.asStateFlow()

    private var lastRetryAction: (() -> Unit)? = null

    protected open fun handleLoading(isLoading: Boolean) {
        _baseUiState.update { it.copy(isLoading = isLoading) }
    }

    protected open fun handleUnexpectedError(
        e: Throwable,
        context: Context? = null,
        retryAction: (() -> Unit)? = null
    ) {
        e.printStackTrace()

        lastRetryAction = retryAction

        if (context != null && !hasInternet(context)) {
            _baseUiState.update {
                it.copy(isConnectionError = true, isLoading = false)
            }
            return
        }

        _baseUiState.update {
            when (e) {
                is java.net.UnknownHostException,
                is java.net.SocketTimeoutException,
                is java.io.IOException -> {
                    it.copy(isConnectionError = true, isLoading = false)
                }
                else -> {
                    it.copy(unexpectedError = e.localizedMessage, isLoading = false)
                }
            }
        }
    }

    fun retryLastAction() {
        clearErrors()
        lastRetryAction?.invoke()
    }

    open fun clearErrors() {
        _baseUiState.update {
            it.copy(
                unexpectedError = null,
                isConnectionError = false
            )
        }
    }

    private fun hasInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}