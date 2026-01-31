package com.example.foodii.core.network

import com.example.foodii.feature.foods.data.datasource.remote.model.CategoryResponse
import com.example.foodii.feature.foods.data.datasource.remote.model.MealDetailResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET("list.php?c=list")
    suspend fun getCategories(): CategoryResponse

    @GET("search.php?f=a")
    suspend fun  searchMealsByFirstLetter(@Query("f") letter:String): MealDetailResponse
}