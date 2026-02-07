package com.example.foodii.core.di

import android.content.Context
import android.util.Log
import com.example.foodii.BuildConfig
import com.example.foodii.core.network.AuthInterceptor
import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.feature.apifoodii.ingredient.data.repositories.IngredientFoodiiRepositoryImpl
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.meal.data.repositories.MealFoodiiRepositoryImpl
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSourceImpl
import com.example.foodii.feature.auth.data.datasource.remote.AuthApi
import com.example.foodii.feature.auth.data.repositories.AuthRepositoryImpl
import com.example.foodii.feature.auth.domain.repository.AuthRepository
import com.example.foodii.feature.apifoodii.meal.di.FoodiiFeatureModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    private val authLocalDataSource: AuthLocalDataSource by lazy {
        AuthLocalDataSourceImpl(context)
    }

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("API_TRAFFIC", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(authLocalDataSource))
        .addInterceptor(loggingInterceptor)
        .build()

    private val foodiiRetrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.FOODII_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val foodiiApi: FoodiiAPI by lazy {
        foodiiRetrofit.create(FoodiiAPI::class.java)
    }

    private val authApi: AuthApi by lazy {
        foodiiRetrofit.create(AuthApi::class.java)
    }

    val foodiiRepository: MealFoodiiRepository by lazy {
        MealFoodiiRepositoryImpl(foodiiApi)
    }

    val ingredientRepository: IngredientRepository by lazy {
        IngredientFoodiiRepositoryImpl(foodiiApi)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApi, authLocalDataSource)
    }

    // Usamos el módulo que tienes abierto para centralizar las factories
    val foodiiFeatureModule: FoodiiFeatureModule by lazy {
        FoodiiFeatureModule(foodiiRepository, ingredientRepository)
    }
}
