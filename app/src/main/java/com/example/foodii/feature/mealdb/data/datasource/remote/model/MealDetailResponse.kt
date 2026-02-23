package com.example.foodii.feature.mealdb.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class MealDetailResponse(
    @SerializedName("meals")
    val meals: List<MealDbDto>?
)
