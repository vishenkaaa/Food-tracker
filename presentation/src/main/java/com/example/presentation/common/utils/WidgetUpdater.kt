package com.example.presentation.common.utils

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.example.presentation.widget.CaloriesWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WidgetUpdater {
    fun updateWidget(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                CaloriesWidget.updateAll(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}