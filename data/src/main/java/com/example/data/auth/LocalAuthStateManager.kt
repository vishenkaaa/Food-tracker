package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalAuthStateManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth
) {
    companion object {
        private const val DATASTORE_NAME = "auth_state"

        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val LAST_SIGN_IN_KEY = longPreferencesKey("last_sign_in")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

    private val dataStore: DataStore<Preferences>
        get() = context.dataStore

    init {
        firebaseAuth.addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null)
                CoroutineScope(Dispatchers.IO).launch {
                    saveAuthState(user.uid, user.email, true)
                }
        }
    }

    suspend fun saveAuthState(userId: String?, email: String?, isLoggedIn: Boolean) {
        try {
            dataStore.edit { preferences ->
                if (userId != null) preferences[USER_ID_KEY] = userId
                else preferences.remove(USER_ID_KEY)

                if (email != null) preferences[USER_EMAIL_KEY] = email
                else preferences.remove(USER_EMAIL_KEY)

                preferences[IS_LOGGED_IN_KEY] = isLoggedIn
                preferences[LAST_SIGN_IN_KEY] = System.currentTimeMillis()
            }
        } catch (e: Exception) {
            Log.e("LocalAuthState", "Error saving auth state", e)
        }
    }

    suspend fun isUserLoggedIn(): Boolean {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null)
            return try {
                firebaseUser.getIdToken(false).await()
                true
            } catch (e: Exception) {
                false
            }

        return try {
            val preferences = dataStore.data.first()

            val isLoggedInBackup = preferences[IS_LOGGED_IN_KEY] ?: false
            val lastSignIn = preferences[LAST_SIGN_IN_KEY] ?: 0
            val userId = preferences[USER_ID_KEY]

            val isNotExpired = System.currentTimeMillis() - lastSignIn < TimeUnit.DAYS.toMillis(7)

            isLoggedInBackup && userId != null && isNotExpired
        } catch (e: Exception) {
            Log.e("LocalAuthState", "Error reading auth state from DataStore", e)
            false
        }
    }

    suspend fun getCurrentUserId(): String? {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null)
            return firebaseUser.uid

        return try {
            val preferences = dataStore.data.first()
            preferences[USER_ID_KEY]
        } catch (e: Exception) {
            Log.e("LocalAuthState", "Error reading user ID from DataStore", e)
            null
        }
    }

    suspend fun clearAuthState() {
        try {
            dataStore.edit { preferences ->
                preferences.clear()
            }
        } catch (e: Exception) {
            Log.e("LocalAuthState", "Error clearing auth state", e)
        }
    }
}