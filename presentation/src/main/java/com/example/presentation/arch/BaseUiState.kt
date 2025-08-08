package com.example.presentation.arch

data class BaseUiState(
    val isLoading: Boolean = false,
    val unexpectedError: String? = null,
    val isConnectionError: Boolean = false
)