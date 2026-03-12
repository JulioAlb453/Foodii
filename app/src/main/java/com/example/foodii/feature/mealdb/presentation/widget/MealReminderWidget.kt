package com.example.foodii.feature.mealdb.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import com.example.foodii.R
import com.example.compose.primaryLight
import com.example.compose.surfaceVariantLight
import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import java.util.*
import androidx.glance.ImageProvider
import androidx.glance.color.ColorProvider
import androidx.glance.text.TextStyle
import com.example.compose.primaryDark
import com.example.ui.theme.TypographyFoodii
import kotlinx.coroutines.flow.firstOrNull


class MealReminderWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetEntryPoint {
        fun plannerRepository(): PlannerRepository
        fun authLocalDataSource(): AuthLocalDataSource
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appContext = context.applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(appContext, WidgetEntryPoint::class.java)
        val repository = entryPoint.plannerRepository()
        val authDataSource = entryPoint.authLocalDataSource()

        val user = authDataSource.getUser().firstOrNull()
        val userId = user?.id ?: ""

        val now = System.currentTimeMillis()

        val plannedMeals = if (userId.isNotEmpty()) {
            repository.getPlannedMealsForDateRange(userId,
                now, now + (48 * 60 * 60 * 1000))
        } else {
            emptyList()
        }
        
        val nextMeal = plannedMeals.firstOrNull()

        provideContent {
            WidgetContent(nextMeal)
        }
    }
    @Composable
    private fun WidgetContent(meal: PlannedMealEntity?) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(surfaceVariantLight)
                .cornerRadius(16.dp)
        ) {
            Image(
                provider = ImageProvider(R.drawable.bg_widget_wave),
                contentDescription = null,
                modifier = GlanceModifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            Row(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.icon_foodii),
                    contentDescription = "Food Icon",
                    modifier = GlanceModifier.size(36.dp)
                )

                Spacer(modifier = GlanceModifier.width(16.dp))

                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    if (meal != null) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val dateString = sdf.format(Date(meal.date))

                        Text(
                            text = meal.name,
                            maxLines = 1,
                            style = TextStyle(
                                fontSize = TypographyFoodii.titleLarge.fontSize,
                                fontWeight = FontWeight.Bold,
                                color = ColorProvider(day = primaryLight, night = primaryLight)
                            )
                        )

                        // Fecha y etiqueta pequeña
                        Text(
                            text = "Próxima comida • $dateString",
                            style = TextStyle(
                                color = ColorProvider(day = primaryLight, night = primaryLight),
                                fontSize = TypographyFoodii.bodySmall.fontSize,
                            )
                        )
                    } else {
                        Text(
                            text = "Sin comidas agendadas",
                            style = TextStyle(
                                fontSize = TypographyFoodii.bodyMedium.fontSize,
                                color = ColorProvider(day = primaryLight, night = primaryDark),
                            )
                        )
                    }
                }

            }

            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .padding(horizontal = 40.dp)
                    .background(primaryLight)
            ) {}
        }
    }
}

class MealReminderWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MealReminderWidget()
}
