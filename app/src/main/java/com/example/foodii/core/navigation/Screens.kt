package com.example.foodii.core.navigation


sealed class Screen(val route: String) {
    object Categories : Screen("categories_screen")
    object Details : Screen("details_screen/{letter}") {
        fun createRoute(letter: String) = "details_screen/$letter"
    }
}