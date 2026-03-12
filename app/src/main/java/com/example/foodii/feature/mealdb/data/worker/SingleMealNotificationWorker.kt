package com.example.foodii.feature.mealdb.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.foodii.core.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SingleMealNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val mealName = inputData.getString("meal_name") ?: "Comida"
        val mealId = inputData.getString("meal_id") ?: ""

        NotificationHelper.showMealAlert(
            applicationContext, 
            "¡Es hora de comer!", 
            "Tu comida '$mealName' está programada para ahora.",
            mealId
        )
        
        return Result.success()
    }
}
