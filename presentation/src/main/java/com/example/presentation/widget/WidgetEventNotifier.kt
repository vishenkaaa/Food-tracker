package com.example.presentation.widget

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.example.presentation.widget.AdaptiveCaloriesWidgetReceiver.Companion.ACTION_CALORIES_UPDATED
import com.example.presentation.widget.AdaptiveCaloriesWidgetReceiver.Companion.ACTION_USER_AUTH_CHANGED

object WidgetEventNotifier {

    fun notifyAuthChanged(context: Context) {
        sendWidgetBroadcasts(
            context,
            ACTION_USER_AUTH_CHANGED
        )
    }

    fun notifyCaloriesUpdated(context: Context) {
        sendWidgetBroadcasts(
            context,
            ACTION_CALORIES_UPDATED
        )
    }

    private fun sendWidgetBroadcasts(context: Context, action: String) {
        val intent = Intent(action).apply {
            component = ComponentName(context, AdaptiveCaloriesWidgetReceiver::class.java)
            setPackage(context.packageName)
        }
        context.sendBroadcast(intent)
    }
}