package com.example.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.presentation.common.utils.WidgetUpdater

class CaloriesSmallWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = CaloriesWidget

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                WidgetUpdater.updateWidget(context)
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WidgetUpdater.updateWidget(context)
    }
}