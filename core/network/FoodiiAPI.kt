package com.example.foodii.core.network

import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model.IngredientDto
import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model.IngredientResponse
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodiiAPI {

    @GET("api/meals")
    suspend fun getMealsAPI(
        @Header("Authorization") token: String,
        @Query("date") date: String? = null
    ): FoodiiMealResponse

    @GET("api/meals/date-range")
    suspend fun getMealsByRange(
        @Header("Authorization") token: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): FoodiiMealResponse

    @GET("api/meals/{id}")
    suspend fun getMealById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): FoodiiMealResponse

    @GET("api/ingredients")
    suspend fun getAllIngredientsAPI(
        @Header("Authorization") token: String
    ): IngredientResponse

    @GET("api/ingredients/{id}")
    suspend fun getIngredientByIdAPI(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): IngredientDto
}
