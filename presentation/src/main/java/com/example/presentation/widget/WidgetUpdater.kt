package com.example.presentation.widget

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import com.example.domain.model.diary.CaloriesProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetUpdater @Inject constructor() {

    suspend fun updateCaloriesWidget(context: Context, caloriesProgress: CaloriesProgress) {
        withContext(Dispatchers.IO) {
            try {
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(CaloriesWidget::class.java)

                if (glanceIds.isEmpty()) {
                    return@withContext
                }

                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(context, glanceId) { prefs ->
                        prefs.toMutablePreferences().apply {
                            this[intPreferencesKey("consumed")] = caloriesProgress.consumed
                            this[intPreferencesKey("target")] = caloriesProgress.target
                        }
                    }
                }

                CaloriesWidget().updateAll(context)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun hasCaloriesWidget(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(CaloriesWidget::class.java)
                glanceIds.isNotEmpty()
            } catch (e: Exception) {
                false
            }
        }
    }
}