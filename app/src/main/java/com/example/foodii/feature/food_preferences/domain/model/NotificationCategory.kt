package com.example.foodii.feature.food_preferences.domain.model

data class NotificationCategory(
    val slug: String,
    val label: String
) {
    companion object {
        val ALL = listOf(
            NotificationCategory("fitness", "Fitness"),
            NotificationCategory("high_protein", "Alto en Proteína"),
            NotificationCategory("low_calorie", "Bajo en Calorías"),
            NotificationCategory("low_carb", "Bajo en Carbohidratos"),
            NotificationCategory("vegan", "Vegano"),
            NotificationCategory("quick_meals", "Comidas Rápidas"),
            NotificationCategory("meal_prep", "Meal Prep"),
            NotificationCategory("family_friendly", "Para la Familia"),
            NotificationCategory("budget_friendly", "Económico"),
            NotificationCategory("gluten_free", "Sin Gluten"),
            NotificationCategory("balanced", "Balanceado"),
            NotificationCategory("healthy_snacks", "Snacks Saludables"),
            NotificationCategory("international", "Internacional")
        )
    }
}
