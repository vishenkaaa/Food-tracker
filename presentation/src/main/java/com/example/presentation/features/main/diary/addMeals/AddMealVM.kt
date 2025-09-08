package com.example.presentation.features.main.diary.addMeals

import com.example.presentation.arch.BaseViewModel
import com.example.presentation.features.main.diary.addMeals.models.AddMealTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddMealVM @Inject constructor() : BaseViewModel() {

    private val _selectedTab = MutableStateFlow(AddMealTab.BARCODE)
    val selectedTab: StateFlow<AddMealTab> = _selectedTab.asStateFlow()

    fun selectTab(tab: AddMealTab) {
        _selectedTab.value = tab
    }
}