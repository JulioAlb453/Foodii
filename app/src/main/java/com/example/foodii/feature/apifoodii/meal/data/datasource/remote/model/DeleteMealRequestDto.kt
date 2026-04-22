package com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class DeleteMealRequestDto(
    @SerializedName("userId") val userId: String
)
