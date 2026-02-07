package com.example.foodii.feature.apifoodii.ingredient.domain.entity

import java.util.Date

data class Ingredient(
    val id: String,
    val name: String,
    val caloriesPer100g: Int,
    val createdBy: String,
    val createdAt: Date
) {

    fun calculateCalories(amount: Int): Int {
        return ((amount.toDouble() * caloriesPer100g) / 100.0).toInt()
    }

    companion object {
        fun create(
            id: String,
            name: String,
            caloriesPer100g: Int,
            createdBy: String,
            createdAt: Date
        ): Ingredient {
            return Ingredient(id, name, caloriesPer100g, createdBy, createdAt)
        }
    }
}