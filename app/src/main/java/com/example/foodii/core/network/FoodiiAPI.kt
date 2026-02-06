package com.example.foodii.core.network

import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodiiAPI {
    
    @GET("meals")
    suspend fun getMeals(): FoodiiMealResponse

    @GET("meals/range")
    suspend fun getMealsByRange(
        @Query("start") startDate: String,
        @Query("end") endDate: String,
        @Query("userId") userId: String
    ): FoodiiMealResponse

    @GET("meals/{id}")
    suspend fun getMealById(@Path("id") id: String): FoodiiMealResponse
}
