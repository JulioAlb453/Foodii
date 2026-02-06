package com.example.foodii.core.di

import IngredientFoodiiRepositoryImpl
import android.content.Context
import com.example.foodii.BuildConfig
import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.core.network.MealApi
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.meal.data.repositories.FoodiiRepositoryImpl
import com.example.foodii.feature.apifoodii.meal.domain.repository.FoodiiRepository
import com.example.foodii.feature.foods.data.datasource.repositories.CategoryRepositoryImpl
import com.example.foodii.feature.foods.domain.repositories.MelCategoryRepository
import com.example.foodii.feature.planner.data.datasource.repositories.PlannerRepositoryImpl
import com.example.foodii.feature.planner.data.local.database.FoodiiDatabase
import com.example.foodii.feature.planner.domain.repository.PlannerRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    // 1. Configuración de Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 2. Base de Datos Local
    private val database: FoodiiDatabase by lazy {
        FoodiiDatabase.getDatabase(context)
    }

    private val plannedMealDao by lazy { database.plannedMealDao() }

    // --- APIS (RETROFIT) ---

    val categoryMelApi: MealApi by lazy {
        retrofit.create(MealApi::class.java)
    }

    // Nueva API para la feature apifoodii
    val foodiiApi: FoodiiAPI by lazy {
        retrofit.create(FoodiiAPI::class.java)
    }

    // --- REPOSITORIOS (SINGLETONS) ---

    val melCategoryRepository: MelCategoryRepository by lazy {
        CategoryRepositoryImpl(categoryMelApi)
    }

    val plannerRepository: PlannerRepository by lazy {
        PlannerRepositoryImpl(plannedMealDao)
    }

    // Nuevo Repositorio de Comidas
    val foodiiRepository: FoodiiRepository by lazy {
        FoodiiRepositoryImpl(foodiiApi)
    }

    // Nuevo Repositorio de Ingredientes
    val ingredientRepository: IngredientRepository by lazy {
        IngredientFoodiiRepositoryImpl(foodiiApi)
    }
}
