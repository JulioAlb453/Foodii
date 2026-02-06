package com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model

data class IngredientResponse(
    val ingredients: List<IngredientDto>
)


data class IngredientDto(
    val id: String?,
    val name: String?,
    val calories_per_100g: Int?,
    val created_by: String?,
    val created_at: Long?
)