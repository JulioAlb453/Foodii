package com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.mapper

import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model.IngredientDto
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
import java.sql.Date

fun IngredientDto.toDomain(): Ingredient {
    return Ingredient(
        id = this.id ?: "",
        name = this.name ?: "",
        caloriesPer100g = this.calories_per_100g ?: 0,
        createdBy = this.created_by ?: "",
        createdAt = Date(this.created_at?: System.currentTimeMillis())
    )
}


fun Ingredient.toDto(): IngredientDto {
    return IngredientDto(
        id = this.id,
        name = this.name,
        calories_per_100g = this.caloriesPer100g,
        created_by = this.createdBy,
        created_at = this.createdAt.time
    )
}


fun List<IngredientDto>.toDomainList(): List<Ingredient> {
    return this.map { it.toDomain() }
}