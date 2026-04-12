package com.example.foodii.core.service.worker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.foodii.feature.mealdb.presentation.widget.MealReminderWidget
import com.example.foodii.BuildConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Calendar
import java.util.concurrent.TimeUnit

// El Worker se ejecuta en background cada 30 minutos
// Descarga una imagen de Pexels según la hora del día y actualiza el widget.

@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val timeOfDay = when (hour) {
                in 6..11 -> "morning"
                in 12..18 -> "afternoon"
                else -> "night"
            }

            val queries = when (timeOfDay) {
                "morning" -> listOf("warm golden sunrise breakfast", "cozy morning sunlight coffee", "beautiful dawn healthy bowl",
                    "soft morning light pancakes", "golden hour breakfast table", "sunrise orange juice fresh",
                    "warm morning kitchen oatmeal", "bright sunrise fruit bowl", "dawn light toast coffee",
                    "golden morning eggs benedict", "cozy sunrise smoothie bowl", "warm sunlight croissant coffee"
                )
                "afternoon" -> listOf(
                    "bright sunny lunch outdoor", "warm sunny day pasta meal", "golden afternoon sandwich picnic",
                    "sunny day salad fresh", "afternoon sunlight burger meal", "bright day tacos colorful",
                    "warm afternoon pizza table", "sunny terrace lunch meal", "golden light sushi plate",
                    "bright afternoon soup bowl", "sunny day grilled food", "warm noon rice bowl"
                )
                else -> listOf(
                    "starry night dinner table", "cozy night city lights pasta", "dark purple night steak dinner",
                    "calm starry sky soup warm", "night moon romantic dinner", "city lights sushi night", "dark evening pizza candle",
                    "night sky wine dinner", "cozy night ramen bowl", "starry night bbq grill",
                    "dark night chocolate dessert", "moonlight dinner seafood"
                )
            }
            val query = queries.random()
            val imageUrl = getPexelsImage(query)
            if (imageUrl != null) {
                val bitmap = getBitmapFromUrl(imageUrl)
                bitmap?.let { saveImageLocally(it, timeOfDay) }
            }
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(MealReminderWidget::class.java)
            glanceIds.forEach { glanceId ->
                MealReminderWidget().update(context, glanceId)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun getPexelsImage(query: String): String? {
        return try {
            val client = OkHttpClient()
            val encodedQuery = query.replace(" ", "%20")
            val request = Request.Builder()
                .url("https://api.pexels.com/v1/search?query=$encodedQuery&per_page=15")
                .addHeader("Authorization", BuildConfig.PEXELS_API_KEY)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            val json = JSONObject(body ?: "")
            val photos = json.getJSONArray("photos")

            if (photos.length() > 0) {
                val randomIndex = (0 until photos.length()).random()
                val src = photos.getJSONObject(randomIndex).getJSONObject("src")
                src.getString("medium")
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.connect()
            BitmapFactory.decodeStream(connection.getInputStream())
        } catch (e: Exception) {
            null
        }
    }
    private fun saveImageLocally(bitmap: Bitmap, timeOfDay: String) {
        val file = File(context.filesDir, "widget_image_$timeOfDay.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
        }
    }
    companion object {
        const val WORK_NAME = "widget_update_work"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
                30, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}