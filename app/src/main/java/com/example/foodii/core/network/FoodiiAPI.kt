package com.example.foodii.core.network

import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model.IngredientDto
import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model.IngredientResponse
import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model.SingleIngredientResponse
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealResponse
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.SingleMealResponse
import com.example.foodii.feature.apifoodii.dish.data.datasource.remote.model.DishDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

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

    @GET("api/meals/random")
    suspend fun getRandomMealAPI(): Response<SingleMealResponse>

    @Multipart
    @POST("api/meals")
    suspend fun createMealAPI(
        @Part("userId") userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("date") date: RequestBody,
        @Part("mealTime") mealTime: RequestBody,
        @Part("ingredients") ingredients: RequestBody,
        @Part image: MultipartBody.Part?
    ): SingleMealResponse

    @GET("api/ingredients")
    suspend fun getAllIngredientsAPI(): IngredientResponse

    @GET("api/ingredients/{id}")
    suspend fun getIngredientByIdAPI(
        @Path("id") id: String
    ): SingleIngredientResponse

    @POST("api/ingredients")
    suspend fun createIngredientAPI(
        @Body ingredient: IngredientDto
    ): SingleIngredientResponse

    @PUT("api/ingredients/{id}")
    suspend fun updateIngredientAPI(
        @Path("id") id: String,
        @Body ingredient: IngredientDto
    ): SingleIngredientResponse

    @DELETE("api/ingredients/{id}")
    suspend fun deleteIngredientAPI(
        @Path("id") id: String
    ): SingleIngredientResponse

    @GET("api/dishes/random")
    suspend fun getRandomDishAPI(): Response<DishDto>
}
