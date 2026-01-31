package com.example.foodii

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.example.compose.FoodiiTheme
import com.example.foodii.core.di.AppContainer
import com.example.foodii.feature.foods.di.CategoryModule
import com.example.foodii.feature.foods.presentation.screen.MelCategoryScreen

class MainActivity : ComponentActivity() {

    lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(this)
        val categoryModule = CategoryModule(appContainer)
        enableEdgeToEdge()
        setContent {
            FoodiiTheme {
                MelCategoryScreen(categoryModule.provideCategoryViewModelFactory())
            }
        }
    }
}

