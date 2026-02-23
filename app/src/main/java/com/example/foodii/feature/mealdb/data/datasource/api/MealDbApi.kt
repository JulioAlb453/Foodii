package com.example.foodii.feature.mealdb.data.datasource.api

import com.example.foodii.feature.mealdb.data.datasource.remote.model.MealDetailResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealDbApi {
    @GET("list.php?c=list")
    suspend fun getCategories(): MealDetailResponse

    @GET("search.php")
    suspend fun searchMealsByFirstLetter(@Query("f") letter: String): MealDetailResponse
}
