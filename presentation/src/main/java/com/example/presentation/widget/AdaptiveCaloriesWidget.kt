package com.example.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
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

object AdaptiveCaloriesWidget : GlanceAppWidget() {

    private const val CONSUMED_KEY = "consumed"
    private const val TARGET_KEY = "target"
    private const val IS_LOADING_KEY = "is_loading"
    private const val HAS_ERROR_KEY = "has_error"
    private const val IS_LOGGED_OUT_KEY = "is_logged_out"

    private const val CONSUMED_CARB_KEY = "consumed_carb"
    private const val TARGET_CARB_KEY = "target_carb"
    private const val CONSUMED_PROTEIN_KEY = "consumed_protein"
    private const val TARGET_PROTEIN_KEY = "target_protein"
    private const val CONSUMED_FAT_KEY = "consumed_fat"
    private const val TARGET_FAT_KEY = "target_fat"

    suspend fun refreshData(context: Context, glanceId: GlanceId) {
        setLoadingState(context, glanceId, true)
        loadFreshData(context, glanceId)
    }

    override val sizeMode = SizeMode.Exact

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
        updateAppWidgetState(
            context,
            PreferencesGlanceStateDefinition,
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
        updateAppWidgetState(
            context,
            PreferencesGlanceStateDefinition,
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
        updateAppWidgetState(
            context,
            PreferencesGlanceStateDefinition,
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
            val getNutritionProgressUseCase = entryPoint.getNutritionProgressUseCase()
            val userId = entryPoint.getCurrentUserIdUseCase()()

            if (userId == null) {
                setLoggedOutState(context, glanceId)
                return
            }

            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val caloriesResult = getCaloriesUseCase(userId, currentDate)
            val nutritionResult = getNutritionProgressUseCase(userId, currentDate)

            if (caloriesResult.isSuccess && nutritionResult.isSuccess) {
                val caloriesProgress = caloriesResult.getOrNull()
                val nutritionProgress = nutritionResult.getOrNull()

                if (caloriesProgress != null && nutritionProgress != null) {
                    updateAppWidgetState(
                        context,
                        PreferencesGlanceStateDefinition,
                        glanceId
                    ) { prefs ->
                        prefs.toMutablePreferences().apply {
                            this[intPreferencesKey(CONSUMED_KEY)] = caloriesProgress.consumed
                            this[intPreferencesKey(TARGET_KEY)] = caloriesProgress.target

                            this[floatPreferencesKey(CONSUMED_CARB_KEY)] = nutritionProgress.consumedCarb
                            this[floatPreferencesKey(TARGET_CARB_KEY)] = nutritionProgress.targetCarb
                            this[floatPreferencesKey(CONSUMED_PROTEIN_KEY)] = nutritionProgress.consumedProtein
                            this[floatPreferencesKey(TARGET_PROTEIN_KEY)] = nutritionProgress.targetProtein
                            this[floatPreferencesKey(CONSUMED_FAT_KEY)] = nutritionProgress.consumedFat
                            this[floatPreferencesKey(TARGET_FAT_KEY)] = nutritionProgress.targetFat

                            this[booleanPreferencesKey(IS_LOADING_KEY)] = false
                            this[booleanPreferencesKey(HAS_ERROR_KEY)] = false
                            this[booleanPreferencesKey(IS_LOGGED_OUT_KEY)] = false
                        }
                    }
                    update(context, glanceId)
                } else {
                    setErrorState(context, glanceId)
                }
            } else {
                setErrorState(context, glanceId)
            }
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

        val consumedCarb = prefs[floatPreferencesKey(CONSUMED_CARB_KEY)] ?: 0f
        val targetCarb = prefs[floatPreferencesKey(TARGET_CARB_KEY)] ?: 0f
        val consumedProtein = prefs[floatPreferencesKey(CONSUMED_PROTEIN_KEY)] ?: 0f
        val targetProtein = prefs[floatPreferencesKey(TARGET_PROTEIN_KEY)] ?: 0f
        val consumedFat = prefs[floatPreferencesKey(CONSUMED_FAT_KEY)] ?: 0f
        val targetFat = prefs[floatPreferencesKey(TARGET_FAT_KEY)] ?: 0f

        val launchMainActivityAction = actionStartActivity(
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        )

        val size = LocalSize.current
        val isLarge = size.width > size.height * 1.5f
        val isMedium = !isLarge && size.width > size.height * 1.1f

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
                else -> {
                    if (isLarge) {
                        LargeWidgetContent(
                            context = context,
                            consumed = consumed,
                            target = target,
                            consumedCarb = consumedCarb,
                            targetCarb = targetCarb,
                            consumedProtein = consumedProtein,
                            targetProtein = targetProtein,
                            consumedFat = consumedFat,
                            targetFat = targetFat
                        )
                    } else {
                        SmallWidgetContent(
                            context = context,
                            consumed = consumed,
                            target = target,
                            isMedium = isMedium
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun LoggedOutContent(context: Context) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = GlanceModifier.fillMaxSize()
        ) {
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
                contentDescription = "App logo",
                modifier = GlanceModifier.size(20.dp),
            )
        }
    }

    @Composable
    private fun LoadingContent(context: Context) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = GlanceModifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier.fillMaxSize()
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
                contentDescription = "App logo",
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
                contentDescription = "App logo",
                modifier = GlanceModifier.size(20.dp),
            )
        }
    }

    @Composable
    private fun SmallWidgetContent(
        context: Context,
        consumed: Int,
        target: Int,
        isMedium: Boolean
    ) {
        val progress = if (target > 0) {
            (consumed.toFloat() / target.toFloat()).coerceIn(0f, 1f)
        } else 0f

        CaloriesProgress(
            context = context,
            progress = progress,
            target = target,
            consumed = consumed,
            isMedium = isMedium,
            modifier = GlanceModifier.fillMaxSize()
        )
    }

    @Composable
    private fun LargeWidgetContent(
        context: Context,
        consumed: Int,
        target: Int,
        consumedCarb: Float,
        targetCarb: Float,
        consumedProtein: Float,
        targetProtein: Float,
        consumedFat: Float,
        targetFat: Float
    ) {
        val progress = if (target > 0) {
            (consumed.toFloat() / target.toFloat()).coerceIn(0f, 1f)
        } else 0f

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier.fillMaxSize()
        ){
            CaloriesProgress(
                context = context,
                consumed = consumed,
                target = target,
                progress = progress,
                modifier = GlanceModifier.size(140.dp)
            )

            Spacer(modifier = GlanceModifier.width(16.dp))

            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MacroProgressBar(
                    label = context.getString(R.string.carbs),
                    consumed = consumedCarb,
                    target = targetCarb,
                    context = context
                )
                Spacer(modifier = GlanceModifier.height(16.dp))
                MacroProgressBar(
                    label = context.getString(R.string.proteins),
                    consumed = consumedProtein,
                    target = targetProtein,
                    context = context
                )
                Spacer(modifier = GlanceModifier.height(16.dp))
                MacroProgressBar(
                    label = context.getString(R.string.fats),
                    consumed = consumedFat,
                    target = targetFat,
                    context = context
                )
            }
        }
    }
}

@Composable
fun MacroProgressBar(
    label: String,
    consumed: Float,
    target: Float,
    context: Context
) {
    Column(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start
        ){
            Text(
                text = label,
                style = TextStyle(
                    color = GlanceTheme.colors.onBackground,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
            )

            Spacer(modifier = GlanceModifier.defaultWeight())

            Text(
                text = context.getString(R.string.grams_format, consumed),
                style = TextStyle(
                    color = GlanceTheme.colors.onBackground,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(8.dp)
                .background(ImageProvider(R.drawable.progress_bar_background)),
        ) {
            val progressPercentage = if (target > 0) {
                (consumed / target).coerceIn(0f, 1f)
            } else 0f

            Box(
                modifier = GlanceModifier
                    .fillMaxHeight()
                    .width((120 * progressPercentage).dp)
                    .background(ImageProvider(R.drawable.progress_bar_foreground)),
            ){}
        }
    }
}

@Composable
fun CaloriesProgress(
    context: Context,
    consumed: Int,
    target: Int,
    progress: Float,
    isMedium: Boolean = false,
    modifier: GlanceModifier
) {
    val bitmap = createProgressCircleBitmap(
        progress = progress,
        sizePx = 300,
        strokeWidthPx = 32f,
        context = context
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Image(
            provider = ImageProvider(bitmap),
            contentDescription = "Calories progress",
            modifier = GlanceModifier.fillMaxSize()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                provider = ImageProvider(R.drawable.lightning),
                contentDescription = "Energy",
                modifier = GlanceModifier.size(
                    if(isMedium) 24.dp else 20.dp)
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            Text(
                text = "$consumed / $target",
                style = TextStyle(
                    color = GlanceTheme.colors.onBackground,
                    fontWeight = FontWeight.Normal,
                    fontSize = if(isMedium) 20.sp else 16.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            Text(
                text = context.getString(R.string.kcal),
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontWeight = FontWeight.Normal,
                    fontSize = if(isMedium) 16.sp else 12.sp
                )
            )
        }
    }
}