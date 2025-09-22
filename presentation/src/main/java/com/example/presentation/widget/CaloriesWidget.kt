package com.example.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
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
import androidx.glance.text.TextStyle
import com.example.presentation.R
import com.example.presentation.common.utils.createProgressCircleBitmap
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object  CaloriesWidget : GlanceAppWidget() {

    private const val CONSUMED_KEY = "consumed"
    private const val TARGET_KEY = "target"
    private const val IS_LOADING_KEY = "is_loading"
    private const val HAS_ERROR_KEY = "has_error"

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

    private fun loadFreshData(context: Context, glanceId: GlanceId) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(
                    context,
                    CaloriesWidgetEntryPoint::class.java
                )

                val getCaloriesUseCase = entryPoint.getCaloriesUseCase()
                val userId = entryPoint.getCurrentUserIdUseCase()()

                if (userId == null) {
                    setErrorState(context, glanceId)
                    return@launch
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
    }

    @Composable
    private fun CaloriesWidgetContent(context: Context) {
        val prefs = currentState<Preferences>()
        val isLoading = prefs[booleanPreferencesKey(IS_LOADING_KEY)] ?: false
        val hasError = prefs[booleanPreferencesKey(HAS_ERROR_KEY)] ?: false
        val consumed = prefs[intPreferencesKey(CONSUMED_KEY)] ?: 0
        val target = prefs[intPreferencesKey(TARGET_KEY)] ?: 2000

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(16.dp)
                .background(GlanceTheme.colors.widgetBackground),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> LoadingContent(context)
                hasError -> ErrorContent(context)
                else -> DataContent(context, consumed, target)
            }
        }
    }

    @Composable
    private fun LoadingContent(context: Context) {
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
                    fontSize = 14.sp
                )
            )
        }
    }

    @Composable
    private fun ErrorContent(context: Context) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = context.getString(R.string.loading_error),
                style = TextStyle(
                    color = GlanceTheme.colors.error,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
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