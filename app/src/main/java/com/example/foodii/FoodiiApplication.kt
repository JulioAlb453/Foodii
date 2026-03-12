package com.example.foodii

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.foodii.core.utils.NotificationHelper
import com.example.foodii.feature.mealdb.data.worker.MealReminderWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class FoodiiApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        setupMealReminderWorker()
    }

    private fun setupMealReminderWorker() {
        val workRequest = PeriodicWorkRequestBuilder<MealReminderWorker>(
            24, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "MealReminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
