package com.example.foodii.feature.apifoodii.meal.data.repositories

import android.net.Uri
import com.example.foodii.core.utils.CloudinaryService
import com.example.foodii.feature.apifoodii.meal.domain.repository.ImageRepository
import javax.inject.Inject

class CloudinaryImageRepositoryImpl @Inject constructor(
    private val cloudinaryService: CloudinaryService
) : ImageRepository {

    override suspend fun uploadImage(uri: Uri): Result<String> {
        return try {
            val url = cloudinaryService.uploadImage(uri)
            if (url != null) {
                Result.success(url)
            } else {
                Result.failure(Exception("Error al subir la imagen a Cloudinary"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
