package com.example.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LargeCaloriesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = LargeCaloriesWidget

    companion object {
        const val ACTION_USER_AUTH_CHANGED = "com.stfalcon.foodsnap.ACTION_USER_AUTH_CHANGED"
        const val ACTION_CALORIES_UPDATED = "com.stfalcon.foodsnap.ACTION_CALORIES_UPDATED"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            ACTION_USER_AUTH_CHANGED,
            ACTION_CALORIES_UPDATED -> {
                updateWidget(context)
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        updateWidget(context)
    }

    private fun updateWidget(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val glanceIds = GlanceAppWidgetManager(context)
                    .getGlanceIds(LargeCaloriesWidget::class.java)

                glanceIds.forEach { glanceId ->
                    LargeCaloriesWidget.refreshData(context, glanceId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}