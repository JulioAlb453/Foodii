package com.example.foodii.core.hardware.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.foodii.core.hardware.domain.CameraManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AndroidCameraManager(private val context: Context) : CameraManager {
    
    override fun capturePhoto(onPhotoCaptured: (Uri?) -> Unit) {
        Log.d("CameraManager", "capturePhoto: Iniciando preparación de hardware...")
        try {
            val photoFile = createImageFile()
            Log.d("CameraManager", "capturePhoto: Archivo creado en ${photoFile.absolutePath}")
            
            val uri = FileProvider.getUriForFile(
                context,
                "com.example.foodii.fileprovider",
                photoFile
            )
            Log.d("CameraManager", "capturePhoto: URI generada correctamente: $uri")
            onPhotoCaptured(uri)
        } catch (e: Exception) {
            Log.e("CameraManager", "Error al preparar archivo: ${e.message}", e)
            Toast.makeText(context, "Error de hardware: ${e.message}", Toast.LENGTH_LONG).show()
            onPhotoCaptured(null)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs()
        }
        
        return File.createTempFile("FOODII_${timeStamp}_", ".jpg", storageDir)
    }
}
