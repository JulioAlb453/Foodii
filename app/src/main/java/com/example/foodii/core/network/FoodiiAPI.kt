package com.example.foodii.core.network

import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model.IngredientResponse
import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model.SingleIngredientResponse
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealResponse
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.SingleMealResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodiiAPI {

    @GET("api/meals")
    suspend fun getMealsAPI(
        @Query("userId") userId: String,
        @Query("date") date: String? = null
    ): FoodiiMealResponse

    @GET("api/meals/date-range")
    suspend fun getMealsByRange(
        @Query("userId") userId: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): FoodiiMealResponse

    @GET("api/meals/{id}")
    suspend fun getMealById(
        @Path("id") id: String
    ): SingleMealResponse

    @GET("api/ingredients")
    suspend fun getAllIngredientsAPI(
        @Query("userId") userId: String
    ): IngredientResponse

    @GET("api/ingredients/{id}")
    suspend fun getIngredientByIdAPI(
        @Path("id") id: String
    ): SingleIngredientResponse
}
