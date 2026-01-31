package com.example.foodii.core.di

import android.content.Context
import com.example.foodii.core.network.MealApi
import com.example.foodii.feature.foods.data.datasource.repositories.CategoryRepositoryImpl
import com.example.foodii.feature.foods.domain.repositories.MelCategoryRepository
import com.example.foodii.feature.planner.data.datasource.repositories.PlannerRepositoryImpl
import com.example.foodii.feature.planner.data.local.database.FoodiiDatabase
import com.example.foodii.feature.planner.domain.repository.PlannerRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer (context: Context){
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.themealdb.com/api/json/v1/1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Usamos el singleton definido en FoodiiDatabase para asegurar que la versión y el nombre sean correctos
    private val database: FoodiiDatabase by lazy {
        FoodiiDatabase.getDatabase(context)
    }
    
    private val plannedMealDao by lazy { database.plannedMealDao() }

    val categoryMelApi: MealApi by lazy{
        retrofit.create(MealApi::class.java)
    }

    val melCategoryRepository: MelCategoryRepository by lazy{
        CategoryRepositoryImpl(categoryMelApi)
    }

    val plannerRepository: PlannerRepository by lazy {
        PlannerRepositoryImpl(plannedMealDao)
    }

}
