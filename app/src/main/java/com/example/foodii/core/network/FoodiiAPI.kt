package com.example.foodii.core.network

import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model.IngredientDto
import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model.IngredientResponse
import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model.SingleIngredientResponse
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.CreateMealRequestDto
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealResponse
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.SingleMealResponse
import com.example.foodii.feature.auth.data.datasource.remote.model.AuthResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodiiAPI {

    @GET("api/meals")
    suspend fun getMealsAPI(
        @Query("date") date: String? = null,
    ): FoodiiMealResponse

    @GET("api/meals/date-range")
    suspend fun getMealsByRange(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): FoodiiMealResponse

    @GET("api/meals/{id}")
    suspend fun getMealById(
        @Path("id") id: String,
    ): SingleMealResponse

    @POST("api/meals")
    suspend fun createMeal(
        @Body body: CreateMealRequestDto,
    ): SingleMealResponse

    @GET("api/ingredients")
    suspend fun getAllIngredientsAPI(): IngredientResponse

    @GET("api/ingredients/{id}")
    suspend fun getIngredientByIdAPI(
        @Path("id") id: String,
    ): SingleIngredientResponse

    @PUT("api/ingredients/{id}")
    suspend fun updateIngredientAPI(
        @Path("id") id: String,
        @Body ingredient: IngredientDto,
    ): SingleIngredientResponse

    @DELETE("api/ingredients/{id}")
    suspend fun deleteIngredientAPI(
        @Path("id") id: String,
    ): SingleIngredientResponse

    @PATCH("api/users/preferences")
    suspend fun updatePreferences(
        @Body request: UpdatePreferencesRequest
    ): AuthResponse
}

data class UpdatePreferencesRequest(
    val notificationCategoryPreferences: List<String>?,
    val fcmToken: String? = null
)
