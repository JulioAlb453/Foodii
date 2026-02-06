package com.example.foodii.core.network

import androidx.room.Delete
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealDto
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface FoodiiAPI {
    @GET("Meals")
    suspend fun getMealsAPI(): FoodiiMealResponse

    @GET("Meals/{id}")
    suspend fun getMealByIdAPI(@Path("id") id: String): FoodiiMealDto

    @GET("Meals")
    suspend fun saveMealAPI(meal: FoodiiMealDto)

    @Delete
    suspend fun deleteMealAPI(id: String)


}