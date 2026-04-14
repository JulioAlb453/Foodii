package com.example.foodii.core.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.foodii.BuildConfig
import java.io.File

fun String?.toFullImageUrl(): String {
    if (this.isNullOrEmpty()) return ""
    return when {
        this.startsWith("http") -> this
        this.startsWith("content://") -> this
        this.startsWith("file://") -> this
        this.contains("cloudinary") -> this
        else -> {
            val cleanPath = if (this.startsWith("/")) this else "/$this"
            "${BuildConfig.FOODII_BASE_URL.removeSuffix("/")}$cleanPath"
        }
    }
}

fun generateTempUri(context: Context): Uri {
    val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}
