package com.example.foodii.feature.mealdb.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.foodii.core.utils.NotificationHelper
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import com.example.foodii.feature.mealdb.domain.usecase.GetPlannedMealsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class MealReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getPlannedMealsUseCase: GetPlannedMealsUseCase,
    private val authLocalDataSource: AuthLocalDataSource
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val user = authLocalDataSource.getUser().firstOrNull()
        val userId = user?.id ?: return Result.failure()

        val tomorrowMeals = getPlannedMealsUseCase.getForTomorrow(userId)
        
        if (tomorrowMeals.isNotEmpty()) {
            NotificationHelper.showMealReminderNotification(applicationContext, tomorrowMeals.size)
        }
        
        return Result.success()
    }
}
