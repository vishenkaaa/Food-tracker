package com.example.common

import android.app.Activity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityHolder @Inject constructor() {
    private var currentActivity: Activity? = null

    fun setActivity(activity: Activity) {
        currentActivity = activity
    }

    fun getActivity(): Activity? {
        return currentActivity
    }

    fun clearActivity() {
        currentActivity = null
    }

    fun requireActivity(): Activity {
        return currentActivity ?: throw IllegalStateException("Activity not set. Call setActivity() first.")
    }
}