package com.example.common

import android.content.Intent

class GoogleSignInPendingException(val intent: Intent) : Exception("Google Sign-In requires UI interaction")
