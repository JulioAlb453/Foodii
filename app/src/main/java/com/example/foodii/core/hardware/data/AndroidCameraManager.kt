package com.example.foodii.core.hardware.data

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.foodii.core.hardware.domain.CameraManager

class AndroidCameraManager(private val context: Context) : CameraManager {
    override fun capturePhoto(onPhotoCaptured: (Uri?) -> Unit) {
        // MENSAJE DE PRUEBA
        Toast.makeText(context, "¡Hardware de Cámara detectado! (Esperando implementación real)", Toast.LENGTH_LONG).show()
        
        // Devolvemos null por ahora
        onPhotoCaptured(null)
    }
}
