package com.example.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.R
import com.example.presentation.common.utils.createProgressCircleBitmap

class CaloriesWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                CaloriesWidgetContent(context)
            }
        }
    }

    @Composable
    private fun CaloriesWidgetContent(context: Context) {
        val prefs = currentState<Preferences>()
        val consumed = prefs[intPreferencesKey("consumed")] ?: 300
        val target = prefs[intPreferencesKey("target")] ?: 2000

        val progress = if (target > 0) {
            (consumed.toFloat() / target.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(8.dp)
                .background(GlanceTheme.colors.widgetBackground),
            contentAlignment = Alignment.Center
        ) {
            val bitmap = createProgressCircleBitmap(
                progress = progress,
                sizePx = 300,
                strokeWidthPx = 32f,
                context = context
            )

            Image(
                provider = ImageProvider(bitmap),
                contentDescription = "Calories progress",
                modifier = GlanceModifier.size(150.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    provider = ImageProvider(R.drawable.lightning),
                    contentDescription = "Energy",
                    modifier = GlanceModifier.size(24.dp)
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
}