package com.example.presentation.widget

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.example.presentation.widget.SmallCaloriesWidgetReceiver.Companion.ACTION_CALORIES_UPDATED
import com.example.presentation.widget.SmallCaloriesWidgetReceiver.Companion.ACTION_USER_AUTH_CHANGED

object WidgetEventNotifier {

    private val WIDGET_RECEIVERS = listOf(
        SmallCaloriesWidgetReceiver::class.java,
        LargeCaloriesWidgetReceiver::class.java
    )

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
        WIDGET_RECEIVERS.forEach { receiverClass ->
            val intent = Intent(action).apply {
                component = ComponentName(
                    context,
                    receiverClass
                )
            }
            context.sendBroadcast(intent)
        }
    }
}