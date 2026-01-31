package com.example.foodii.core.network

import com.example.foodii.feature.foods.data.datasource.remote.model.CategoryResponse
import retrofit2.http.GET

interface MealApi {
    @GET("list.php?c=list")
    suspend fun getCategories(): CategoryResponse
}