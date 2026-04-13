package com.example.foodii.core.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.foodii.MainActivity
import com.example.foodii.R
import com.example.foodii.feature.auth.domain.repository.AuthRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FoodiiFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var authRepository: AuthRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "¡Mensaje FCM recibido!")

        val mealId = remoteMessage.data["mealId"]
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Foodii"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "¡Echa un vistazo a lo nuevo!"

        sendNotification(title, body, mealId)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        scope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                authRepository.updatePreferences(user.notificationCategoryPreferences, token)
            }
        }
    }

    private fun sendNotification(title: String, messageBody: String, mealId: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            if (mealId != null) {
                putExtra("mealId", mealId)
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) 
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones de Foodii",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // CORRECCIÓN: Usar el hash del mealId como ID de notificación.
        // Si el mealId es el mismo, la notificación se sobrescribe en lugar de duplicarse.
        val notificationId = mealId?.hashCode() ?: System.currentTimeMillis().toInt()
        
        Log.d(TAG, "Mostrando notificación para mealId: $mealId con ID: $notificationId")
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    companion object {
        private const val TAG = "FoodiiFCMService"
    }
}
