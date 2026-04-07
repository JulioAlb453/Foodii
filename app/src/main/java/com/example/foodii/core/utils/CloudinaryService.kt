package com.example.foodii.core.utils

import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.foodii.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

interface CloudinaryService {
    suspend fun uploadImage(uri: Uri): String?
}

@Singleton
class CloudinaryServiceImpl @Inject constructor(
    private val mediaManager: MediaManager
) : CloudinaryService {

    override suspend fun uploadImage(uri: Uri): String? = suspendCancellableCoroutine { continuation ->
        try {
            Log.d("CloudinaryService", "Iniciando upload para URI: $uri")

            val requestId = mediaManager.upload(uri)
                .unsigned(BuildConfig.CLOUDINARY_UPLOAD_PRESET)
                .option("folder", "foodii_meals")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Log.d("CloudinaryService", "Upload iniciado: $requestId")
                    }
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        Log.d("CloudinaryService", "Progreso: $bytes / $totalBytes")
                    }
                    
                    override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                        val url = resultData?.get("secure_url") as? String
                        Log.d("CloudinaryService", "Upload exitoso: $url")
                        if (continuation.isActive) {
                            continuation.resume(url)
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.e("CloudinaryService", "Error en upload: code=${error?.code}, message=${error?.description}")
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        Log.w("CloudinaryService", "Upload reprogramado: ${error?.description}")
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }
                })
                .dispatch()
            continuation.invokeOnCancellation {
                Log.d("CloudinaryService", "Cancelando upload: $requestId")
                mediaManager.cancelRequest(requestId)
            }
        } catch (e: Exception) {
            Log.e("CloudinaryService", "Excepción durante el upload: ${e.message}", e)
            if (continuation.isActive) {
                continuation.resume(null)
            }
        }
    }
}
