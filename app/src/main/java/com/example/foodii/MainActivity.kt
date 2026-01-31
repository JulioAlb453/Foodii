package com.example.foodii

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import com.example.compose.FoodiiTheme
import com.example.foodii.core.di.AppContainer
import com.example.foodii.feature.foods.di.CategoryModule
import com.example.foodii.feature.foods.presentation.screen.MelCategoryScreen
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.foodii.feature.foods.di.FoodDetailsModule
import com.example.foodii.feature.foods.presentation.screen.MealDetailsScreen

class MainActivity : ComponentActivity() {

    lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(this)
        val categoryModule = CategoryModule(appContainer)
        val foodDetailsModule = FoodDetailsModule(appContainer)

        enableEdgeToEdge()
        setContent {
            FoodiiTheme {
                val navController = rememberNavController()
                val detailsModule = FoodDetailsModule(appContainer)

                NavHost(
                    navController = navController,
                    startDestination = "details/a"
                ) {
                    composable(
                        route = "details/{letter}",
                        arguments = listOf(navArgument("letter") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val letter = backStackEntry.arguments?.getString("letter") ?: "a"
                        val factory = detailsModule.provideMealDetailsViewModelFactory(letter)

                        MealDetailsScreen(factory = factory)
                    }
                }
            }
        }
    }
}

