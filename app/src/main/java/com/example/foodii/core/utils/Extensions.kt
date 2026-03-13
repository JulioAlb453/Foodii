package com.example.foodii.core.utils

import com.example.foodii.BuildConfig

fun String?.toFullImageUrl(): String {
    if (this.isNullOrEmpty()) return ""
    return when {
        this.startsWith("http") -> this
        this.startsWith("content://") -> this
        this.startsWith("file://") -> this
        else -> {
            val cleanPath = if (this.startsWith("/")) this else "/$this"
            "${BuildConfig.FOODII_BASE_URL.removeSuffix("/")}$cleanPath"
        }
    }
}
