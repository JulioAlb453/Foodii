package com.example.foodii.core.di

import android.content.Context
import com.example.foodii.core.network.MealApi
import com.example.foodii.feature.foods.data.datasource.repositories.CategoryRepositoryImpl
import com.example.foodii.feature.foods.domain.repositories.MelCategoryRepository
import com.example.foodii.feature.foods.presentation.viewmodel.GetMealInstructionsUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer (context: Context){
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.themealdb.com/api/json/v1/1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val categoryMelApi: MealApi by lazy{
        retrofit.create(MealApi::class.java)
    }

    val melCategoryRepository: MelCategoryRepository by lazy{
        CategoryRepositoryImpl(categoryMelApi)
    }

    val getMealInstructionsUseCase by lazy {
        GetMealInstructionsUseCase(melCategoryRepository)
    }
}