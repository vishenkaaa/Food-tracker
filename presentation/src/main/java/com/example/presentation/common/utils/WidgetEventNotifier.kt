package com.example.presentation.common.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.example.presentation.widget.CaloriesSmallWidgetReceiver

object WidgetEventNotifier {

    fun notifyAuthChanged(context: Context) {
        sendWidgetBroadcast(
            context,
            CaloriesSmallWidgetReceiver.ACTION_USER_AUTH_CHANGED
        )
    }

    fun notifyCaloriesUpdated(context: Context) {
        sendWidgetBroadcast(
            context,
            CaloriesSmallWidgetReceiver.ACTION_CALORIES_UPDATED
        )
    }

    private fun sendWidgetBroadcast(context: Context, action: String) {
        val intent = Intent(action).apply {
            component = ComponentName(
                context,
                CaloriesSmallWidgetReceiver::class.java
            )
        }
        context.sendBroadcast(intent)
    }
}