package com.example.foodii.feature.mealdb.presentation.widget

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import com.example.foodii.R
import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.core.service.worker.WidgetUpdateWorker
import com.example.foodii.MainActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            WidgetEntryPoint::class.java
        )

        val repository = entryPoint.plannerRepository()
        val authDataSource = entryPoint.authLocalDataSource()
        val mealRepository = entryPoint.mealRepository()

        val user = authDataSource.getUser().firstOrNull()
        val userId = user?.id ?: ""

        val now = System.currentTimeMillis()
        val plannedMeals = if (userId.isNotEmpty()) {
            repository.getPlannedMealsForDateRange(userId, now, now + (48 * 60 * 60 * 1000))
        } else {
            emptyList()
        }

        val nextPlanned = plannedMeals.firstOrNull()
        val fullMealDetail = nextPlanned?.let { mealRepository.getMealById(it.mealId, userId) }

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("mealId", nextPlanned?.mealId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timeOfDay = when (hour) {
            in 6..11 -> "morning"
            in 12..18 -> "afternoon"
            else -> "night"
        }

        val imageFile = File(context.filesDir, "widget_image_$timeOfDay.png")
        val bitmap = if (imageFile.exists()) {
            BitmapFactory.decodeFile(imageFile.absolutePath)
        } else null

        provideContent {
            WidgetContent(
                planned = nextPlanned,
                detail = fullMealDetail,
                intent = intent,
                bitmap = bitmap,
                timeOfDay = timeOfDay
            )
        }
    }

    @Composable
    private fun WidgetContent(
        planned: PlannedMealEntity?,
        detail: FoodiiMeal?,
        intent: Intent,
        bitmap: android.graphics.Bitmap?,
        timeOfDay: String
    ) {
        val backgroundColorRes = when (timeOfDay) {
            "morning" -> R.color.morning_color
            "afternoon" -> R.color.afternoon_color
            else -> R.color.night_color
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(backgroundColorRes)
                .cornerRadius(16.dp)
                .clickable(actionStartActivity(intent))
        ) {
            bitmap?.let {
                Image(
                    provider = ImageProvider(it),
                    contentDescription = null,
                    modifier = GlanceModifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(R.color.widget_overlay)
            ) {}

            Row(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Column(
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    if (planned != null) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val dateString = sdf.format(Date(planned.date))

                        Text(
                            text = planned.name,
                            maxLines = 1,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                color = ColorProvider(R.color.white)
                            )
                        )

                        Text(
                            text = detail?.instructions ?: "Toca para ver la receta",
                            maxLines = 1,
                            style = TextStyle(
                                color = ColorProvider(R.color.white)
                            )
                        )

                        Text(
                            text = "Próxima comida • $dateString",
                            style = TextStyle(
                                color = ColorProvider(R.color.white)
                            )
                        )
                    } else {
                        Text(
                            text = "Sin comidas agendadas",
                            style = TextStyle(
                                color = ColorProvider(R.color.white)
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