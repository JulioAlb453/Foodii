package com.example.foodii.core.network

import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealResponse
import retrofit2.http.GET

interface FoodiiAPI {
    @GET("Meals")
    suspend fun getMealsAPI(): FoodiiMealResponse

}