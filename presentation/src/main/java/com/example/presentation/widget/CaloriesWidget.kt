package com.example.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.example.presentation.R
import com.example.presentation.common.utils.createProgressCircleBitmap
import com.example.presentation.features.main.MainActivity
import dagger.hilt.android.EntryPointAccessors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CaloriesWidget : GlanceAppWidget() {

    private const val CONSUMED_KEY = "consumed"
    private const val TARGET_KEY = "target"
    private const val IS_LOADING_KEY = "is_loading"
    private const val HAS_ERROR_KEY = "has_error"
    private const val IS_LOGGED_OUT_KEY = "is_logged_out"

    suspend fun refreshData(context: Context, glanceId: GlanceId) {
        setLoadingState(context, glanceId, true)
        loadFreshData(context, glanceId)
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        setLoadingState(context, id, true)
        loadFreshData(context, id)

        provideContent {
            GlanceTheme {
                CaloriesWidgetContent(context)
            }
        }
    }

    private suspend fun setLoadingState(context: Context, glanceId: GlanceId, isLoading: Boolean) {
        androidx.glance.appwidget.state.updateAppWidgetState(
            context,
            androidx.glance.state.PreferencesGlanceStateDefinition,
            glanceId
        ) { prefs ->
            prefs.toMutablePreferences().apply {
                this[booleanPreferencesKey(IS_LOADING_KEY)] = isLoading
                if (!isLoading) {
                    this[booleanPreferencesKey(HAS_ERROR_KEY)] = false
                }
            }
        }
        update(context, glanceId)
    }

    private suspend fun setErrorState(context: Context, glanceId: GlanceId) {
        androidx.glance.appwidget.state.updateAppWidgetState(
            context,
            androidx.glance.state.PreferencesGlanceStateDefinition,
            glanceId
        ) { prefs ->
            prefs.toMutablePreferences().apply {
                this[booleanPreferencesKey(IS_LOADING_KEY)] = false
                this[booleanPreferencesKey(HAS_ERROR_KEY)] = true
            }
        }
        update(context, glanceId)
    }

    private suspend fun setLoggedOutState(context: Context, glanceId: GlanceId) {
        androidx.glance.appwidget.state.updateAppWidgetState(
            context,
            androidx.glance.state.PreferencesGlanceStateDefinition,
            glanceId
        ) { prefs ->
            prefs.toMutablePreferences().apply {
                this[booleanPreferencesKey(IS_LOADING_KEY)] = false
                this[booleanPreferencesKey(HAS_ERROR_KEY)] = false
                this[booleanPreferencesKey(IS_LOGGED_OUT_KEY)] = true
            }
        }
        update(context, glanceId)
    }

    private suspend fun loadFreshData(context: Context, glanceId: GlanceId) {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(
                context,
                CaloriesWidgetEntryPoint::class.java
            )

            val getCaloriesUseCase = entryPoint.getCaloriesUseCase()
            val userId = entryPoint.getCurrentUserIdUseCase()()

            if (userId == null) {
                setLoggedOutState(context, glanceId)
                return
            }

            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val caloriesResult = getCaloriesUseCase(userId, currentDate)

            caloriesResult.fold(
                onSuccess = { caloriesProgress ->
                    androidx.glance.appwidget.state.updateAppWidgetState(
                        context,
                        androidx.glance.state.PreferencesGlanceStateDefinition,
                        glanceId
                    ) { prefs ->
                        prefs.toMutablePreferences().apply {
                            this[intPreferencesKey(CONSUMED_KEY)] = caloriesProgress.consumed
                            this[intPreferencesKey(TARGET_KEY)] = caloriesProgress.target
                            this[booleanPreferencesKey(IS_LOADING_KEY)] = false
                            this[booleanPreferencesKey(HAS_ERROR_KEY)] = false
                            this[booleanPreferencesKey(IS_LOGGED_OUT_KEY)] = false
                        }
                    }
                    update(context, glanceId)
                },
                onFailure = { error ->
                    error.printStackTrace()
                    setErrorState(context, glanceId)
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            setErrorState(context, glanceId)
        }
    }

    @Composable
    private fun CaloriesWidgetContent(context: Context) {
        val prefs = currentState<Preferences>()
        val isLoading = prefs[booleanPreferencesKey(IS_LOADING_KEY)] ?: false
        val hasError = prefs[booleanPreferencesKey(HAS_ERROR_KEY)] ?: false
        val isLoggedOut = prefs[booleanPreferencesKey(IS_LOGGED_OUT_KEY)] ?: false
        val consumed = prefs[intPreferencesKey(CONSUMED_KEY)] ?: 0
        val target = prefs[intPreferencesKey(TARGET_KEY)] ?: 0

        val launchMainActivityAction = actionStartActivity(
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        )

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(16.dp)
                .background(GlanceTheme.colors.widgetBackground)
                .clickable(launchMainActivityAction),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> LoadingContent(context)
                hasError -> ErrorContent(context)
                isLoggedOut -> LoggedOutContent(context)
                else -> DataContent(context, consumed, target)
            }
        }
    }

    @Composable
    private fun LoggedOutContent(context: Context) {
        Box(contentAlignment = Alignment.TopEnd){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = GlanceModifier.fillMaxSize().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_lock),
                    contentDescription = "Logged out",
                    modifier = GlanceModifier.size(32.dp),
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.primary)
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                Text(
                    text = context.getString(R.string.login_required),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    ),
                )
            }

            Image(
                provider = ImageProvider(R.drawable.app_logo),
                contentDescription = "Logged out",
                modifier = GlanceModifier.size(20.dp),
            )
        }
    }

    @Composable
    private fun LoadingContent(context: Context) {
        Box(contentAlignment = Alignment.TopEnd) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = GlanceTheme.colors.primary,
                    modifier = GlanceModifier.size(48.dp)
                )

                Spacer(modifier = GlanceModifier.height(8.dp))

                Text(
                    text = context.getString(R.string.loading),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    )
                )
            }
            Image(
                provider = ImageProvider(R.drawable.app_logo),
                contentDescription = "Logged out",
                modifier = GlanceModifier.size(20.dp),
            )
        }
    }

    @Composable
    private fun ErrorContent(context: Context) {
        Box(contentAlignment = Alignment.TopEnd) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = context.getString(R.string.oops),
                style = TextStyle(
                    color = GlanceTheme.colors.error,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            Text(
                text = context.getString(R.string.loading_error),
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
            Image(
                provider = ImageProvider(R.drawable.app_logo),
                contentDescription = "Logged out",
                modifier = GlanceModifier.size(20.dp),
            )
        }
    }

    @Composable
    private fun DataContent(context: Context, consumed: Int, target: Int) {
        val progress = if (target > 0) {
            (consumed.toFloat() / target.toFloat()).coerceIn(0f, 1f)
        } else 0f

        val bitmap = createProgressCircleBitmap(
            progress = progress,
            sizePx = 300,
            strokeWidthPx = 32f,
            context = context
        )

        Image(
            provider = ImageProvider(bitmap),
            contentDescription = "Calories progress",
            modifier = GlanceModifier.fillMaxSize()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = GlanceModifier
        ) {

            Image(
                provider = ImageProvider(R.drawable.lightning),
                contentDescription = "Energy",
                modifier = GlanceModifier.size(20.dp)
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            Text(
                text = "$consumed / $target",
                style = TextStyle(
                    color = GlanceTheme.colors.onBackground,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            Text(
                text = context.getString(R.string.kcal),
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            )
        }
    }
}