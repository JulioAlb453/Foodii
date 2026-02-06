package com.example.foodii.core.network

import androidx.room.Delete
import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model.IngredientDto
import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model.IngredientResponse
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealDto
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface FoodiiAPI {
    @GET("Ingredients")
    suspend fun getAllIngredientsAPI(
        @Query("userId") userId: String
    ): IngredientResponse


    @GET("Ingredients/{id}")
    suspend fun getIngredientByIdAPI(
        @Path("id") id: String
    ): IngredientDto


    @GET("Meals")
    suspend fun getMealsAPI(
        @Query("userId") userId: String
    ): FoodiiMealResponse


    @GET("Meals/{id}")
    suspend fun getMealByIdAPI(
        @Path("id") id: String
    ): FoodiiMealDto

}