package com.example.foodii.core.di

import IngredientFoodiiRepositoryImpl
import android.content.Context
import com.example.foodii.BuildConfig
import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.core.network.MealApi
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.meal.data.repositories.FoodiiRepositoryImpl
import com.example.foodii.feature.apifoodii.meal.domain.repository.FoodiiRepository
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSourceImpl
import com.example.foodii.feature.auth.data.datasource.remote.AuthApi
import com.example.foodii.feature.auth.data.repositories.AuthRepositoryImpl
import com.example.foodii.feature.auth.domain.repository.AuthRepository
import com.example.foodii.feature.foods.data.datasource.repositories.CategoryRepositoryImpl
import com.example.foodii.feature.foods.domain.repositories.MelCategoryRepository
import com.example.foodii.feature.planner.data.datasource.repositories.PlannerRepositoryImpl
import com.example.foodii.feature.planner.data.local.database.FoodiiDatabase
import com.example.foodii.feature.planner.domain.repository.PlannerRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val database: FoodiiDatabase by lazy {
        FoodiiDatabase.getDatabase(context)
    }

    private val plannedMealDao by lazy { database.plannedMealDao() }


    val categoryMelApi: MealApi by lazy {
        retrofit.create(MealApi::class.java)
    }

    val foodiiApi: FoodiiAPI by lazy {
        retrofit.create(FoodiiAPI::class.java)
    }

    private val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }


    private val authLocalDataSource: AuthLocalDataSource by lazy {
        AuthLocalDataSourceImpl(context)
    }


    val melCategoryRepository: MelCategoryRepository by lazy {
        CategoryRepositoryImpl(categoryMelApi)
    }

    val plannerRepository: PlannerRepository by lazy {
        PlannerRepositoryImpl(plannedMealDao)
    }

    val foodiiRepository: FoodiiRepository by lazy {
        FoodiiRepositoryImpl(foodiiApi)
    }

    val ingredientRepository: IngredientRepository by lazy {
        IngredientFoodiiRepositoryImpl(foodiiApi)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApi, authLocalDataSource)
    }
}
