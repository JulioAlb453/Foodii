package com.example.foodii

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.foodii.core.utils.NotificationHelper
import com.example.foodii.feature.mealdb.data.worker.MealReminderWorker
import com.google.firebase.messaging.FirebaseMessaging
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
        
        // LOG CRÍTICO: Verificar si Firebase está funcionando y obtener el Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM_INIT", "FCM Token actual: ${task.result}")
            } else {
                Log.e("FCM_INIT", "Fallo al obtener el Token FCM. ¿Registraste el SHA-1 en Firebase?", task.exception)
            }
        }
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
