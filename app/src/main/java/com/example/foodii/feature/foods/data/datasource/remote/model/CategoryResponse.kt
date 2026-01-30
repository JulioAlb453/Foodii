package com.example.foodii.feature.foods.data.datasource.remote.model

data class CategoryResponse (
    val meals: List<CategoryDTO>
)

data class CategoryDTO (
    val strCategory: String,
    )