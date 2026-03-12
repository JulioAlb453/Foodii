package com.example.foodii.core.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.foodii.MainActivity

object NotificationHelper {
    private const val CHANNEL_ID = "meal_reminder_channel"
    private const val ALERT_CHANNEL_ID = "meal_alert_channel"
    private const val CHANNEL_NAME = "Recordatorios de Comidas"
    private const val ALERT_CHANNEL_NAME = "Alertas de Comida Próxima"
    private const val REMINDER_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Canal para recordatorios diarios (Baja importancia)
            val reminderChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Canal para los recordatorios de comidas del día siguiente"
            }
            notificationManager.createNotificationChannel(reminderChannel)

            // Canal para alertas próximas (Alta importancia + Sonido)
            val alertChannel = NotificationChannel(ALERT_CHANNEL_ID, ALERT_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Canal para avisarte cuando es hora de comer"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(alertChannel)
        }
    }

    fun showMealReminderNotification(context: Context, mealCount: Int) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Agenda para mañana")
            .setContentText("Tienes $mealCount platillos planeados para mañana.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(REMINDER_ID, builder.build())
    }

    fun showMealAlert(context: Context, title: String, message: String, mealId: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("mealId", mealId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, mealId.hashCode(), intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(mealId.hashCode(), builder.build())
    }
}
