package com.example.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import androidx.glance.appwidget.state.updateAppWidgetState
import com.example.presentation.R
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CaloriesSmallWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget by lazy {
        CaloriesWidget()
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            CaloriesWidgetEntryPoint::class.java
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateWidgetData(context, entryPoint)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun updateWidgetData(
        context: Context,
        entryPoint: CaloriesWidgetEntryPoint
    ) {
        val getCaloriesUseCase = entryPoint.getCaloriesUseCase()

        val userId = entryPoint.getCurrentUserIdUseCase()() ?: throw Exception(context.getString(R.string.user_not_authenticated))
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val caloriesResult = getCaloriesUseCase(userId, currentDate)

        caloriesResult.fold(
            onSuccess = { caloriesProgress ->
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(CaloriesWidget::class.java)

                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(context, glanceId) { prefs ->
                        prefs.toMutablePreferences().apply {
                            this[intPreferencesKey("consumed")] = caloriesProgress.consumed
                            this[intPreferencesKey("target")] = caloriesProgress.target
                        }
                    }
                }

                glanceAppWidget.updateAll(context)
            },
            onFailure = { error ->
                error.printStackTrace()
            }
        )
    }
}