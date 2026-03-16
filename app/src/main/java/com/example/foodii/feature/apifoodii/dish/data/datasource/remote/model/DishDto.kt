package com.example.foodii.feature.apifoodii.dish.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class DishDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("calories") val calories: Double?,
    @SerializedName("image") val image: String?,
    @SerializedName("createdBy") val createdBy: String?,
    @SerializedName("createdAt") val createdAt: String?
)
