package com.example.presentation.features.main.diary.addMeals.addMealsAI.dishLoading

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.domain.model.diary.Dish
import com.example.domain.usecase.gpt.AnalyzeDishImageUseCase
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.common.utils.uriToBase64
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DishLoadingVM @Inject constructor(
    private val analyzeDishImageUseCase: AnalyzeDishImageUseCase,
    @ApplicationContext private val context: Context,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(DishLoadingUiState())
    val uiState: StateFlow<DishLoadingUiState> = _uiState.asStateFlow()

    fun analyze(imgUri: String) {
        viewModelScope.launch {
            handleLoading(true)

            val base64 = withContext(Dispatchers.IO) {
                uriToBase64(context, imgUri)
            }

            val result = withContext(Dispatchers.IO) {
                analyzeDishImageUseCase(base64)
            }

            result
                .onSuccess { dishes ->
                    _uiState.update { it.copy(dishes = dishes) }
                }
                .also {
                    _uiState.update { it.copy(loading = false) }
                }
        }
    }
}

data class DishLoadingUiState(
    val dishes: List<Dish> = emptyList(),
    val loading: Boolean = true
)
