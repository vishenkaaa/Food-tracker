package com.example.presentation.features.main.diary.addMeals.addMealsAI.dishLoading

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    companion object {
        private const val KEY_IMG_URI = "imgUri"
    }

    private val _uiState = MutableStateFlow(DishLoadingUiState())
    val uiState: StateFlow<DishLoadingUiState> = _uiState.asStateFlow()

    init {
        val imgUri: String? = savedStateHandle[KEY_IMG_URI]
        if (imgUri != null) {
            analyze(imgUri)
        }
    }

    private fun analyze(imgUri: String) {
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
                    Log.d("DishLoadingVM", "GPT response: $dishes")
                    _uiState.update { it.copy(dishes = dishes) }
                }
                .onFailure { e ->
                    Log.e("DishLoadingVM", "GPT error", e)
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
