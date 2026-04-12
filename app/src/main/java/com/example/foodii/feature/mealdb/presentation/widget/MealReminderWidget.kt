package com.example.foodii.feature.mealdb.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
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
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.MainActivity
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
        fun mealRepository(): MealFoodiiRepository
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appContext = context.applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(appContext, WidgetEntryPoint::class.java)
        val repository = entryPoint.plannerRepository()
        val authDataSource = entryPoint.authLocalDataSource()
        val mealRepository = entryPoint.mealRepository()
        val user = authDataSource.getUser().firstOrNull()
        val userId = user?.id ?: ""

        val now = System.currentTimeMillis()
        val plannedMeals = if (userId.isNotEmpty()) {
            repository.getPlannedMealsForDateRange(userId, now, now + (24L * 60 * 60 * 1000))
        } else {
            emptyList()
        }
        
        val nextPlanned = plannedMeals.sortedBy { it.date }.firstOrNull()
        val fullMealDetail = nextPlanned?.let { mealRepository.getMealById(it.mealId, userId) }

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("mealId", nextPlanned?.mealId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        provideContent {
            WidgetContent(nextPlanned, fullMealDetail, intent)
        }
    }

    @Composable
    private fun WidgetContent(planned: PlannedMealEntity?, detail: FoodiiMeal?, intent: Intent) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(surfaceVariantLight)
                .cornerRadius(16.dp)
                .clickable(actionStartActivity(intent))
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
                    if (planned != null) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        val dateString = sdf.format(Date(planned.date))

                        Text(
                            text = planned.name,
                            maxLines = 1,
                            style = TextStyle(
                                fontSize = TypographyFoodii.titleLarge.fontSize,
                                fontWeight = FontWeight.Bold,
                                color = ColorProvider(day = primaryLight, night = primaryLight)
                            )
                        )

                        val instructionsText = if (detail != null && detail.stepsPlainText().isNotEmpty()) {
                            detail.stepsPlainText()
                        } else {
                            "Toca para ver la receta"
                        }

                        Text(
                            text = instructionsText,
                            maxLines = 1,
                            style = TextStyle(
                                fontSize = TypographyFoodii.bodySmall.fontSize,
                                color = ColorProvider(day = primaryLight.copy(alpha = 0.7f), night = primaryLight)
                            )
                        )

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
        }
    }
}

class MealReminderWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MealReminderWidget()
}
