package com.example.foodii.feature.apifoodii.meal.domain.repository

import android.net.Uri


interface ImageRepository {

    suspend fun uploadImage(uri: Uri): Result<String>
}
