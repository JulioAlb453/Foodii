package com.example.foodii.core.hardware.domain

import android.net.Uri

interface CameraManager {
    fun capturePhoto(onPhotoCaptured: (Uri?) -> Unit)
}
