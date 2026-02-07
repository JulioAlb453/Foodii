package com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.mapper

import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.model.IngredientDto
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun IngredientDto.toDomain(): Ingredient {
    return Ingredient(
        id = this.id ?: "",
        name = this.name ?: "",
        caloriesPer100g = this.caloriesPer100g ?: 0.0,
        createdBy = this.createdBy ?: "",
        createdAt = try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            format.parse(this.createdAt ?: "")
        } catch (e: Exception) {
            null
        }
    )
}

fun Ingredient.toDto(): IngredientDto {
    return IngredientDto(
        id = this.id,
        name = this.name,
        caloriesPer100g = this.caloriesPer100g,
        createdBy = this.createdBy,
        createdAt = this.createdAt?.toString()
    )
}

fun List<IngredientDto>.toDomainList(): List<Ingredient> {
    return this.map { it.toDomain() }
}
