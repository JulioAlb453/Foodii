package com.example.foodii.feature.mealdb.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.foodii.core.utils.NotificationHelper
import com.example.foodii.feature.mealdb.domain.usecase.GetPlannedMealsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MealReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getPlannedMealsUseCase: GetPlannedMealsUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val tomorrowMeals = getPlannedMealsUseCase.getForTomorrow()
        
        if (tomorrowMeals.isNotEmpty()) {
            NotificationHelper.showMealReminderNotification(applicationContext, tomorrowMeals.size)
            // TODO: Update Glance Widget here
        }
        
        return Result.success()
    }
}
