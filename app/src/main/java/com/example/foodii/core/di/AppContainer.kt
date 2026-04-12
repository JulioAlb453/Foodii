package com.example.foodii.core.di

import android.content.Context
import android.util.Log
import com.cloudinary.android.MediaManager
import com.example.foodii.BuildConfig
import com.example.foodii.core.network.AuthInterceptor
import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.core.hardware.data.AndroidCameraManager
import com.example.foodii.core.hardware.data.AndroidShakeDetector
import com.example.foodii.core.hardware.domain.CameraManager
import com.example.foodii.core.hardware.domain.ShakeDetector
import com.example.foodii.feature.apifoodii.ingredient.data.repositories.IngredientFoodiiRepositoryImpl
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.meal.data.repositories.MealFoodiiRepositoryImpl
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSourceImpl
import com.example.foodii.feature.auth.data.datasource.remote.AuthApi
import com.example.foodii.feature.auth.data.repositories.AuthRepositoryImpl
import com.example.foodii.feature.auth.domain.repository.AuthRepository
import com.example.foodii.feature.apifoodii.meal.di.MealFoodiiModule
import com.example.foodii.feature.apifoodii.ingredient.di.IngredientFoodiiModule
import com.example.foodii.feature.mealdb.data.datasource.api.MealDbApi
import com.example.foodii.feature.mealdb.data.datasource.repositories.PlannerRepositoryImpl
import com.example.foodii.feature.mealdb.data.local.database.FoodiiDatabase
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import com.example.foodii.core.utils.CloudinaryService
import com.example.foodii.core.utils.CloudinaryServiceImpl
import com.example.foodii.feature.apifoodii.meal.data.repositories.CloudinaryImageRepositoryImpl
import com.example.foodii.feature.apifoodii.meal.domain.repository.ImageRepository
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

    private val mealDbRetrofit = Retrofit.Builder()
        .baseUrl("https://www.themealdb.com/api/json/v1/1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val foodiiApi: FoodiiAPI by lazy {
        foodiiRetrofit.create(FoodiiAPI::class.java)
    }

    private val authApi: AuthApi by lazy {
        foodiiRetrofit.create(AuthApi::class.java)
    }

    private val mealDbApi: MealDbApi by lazy {
        mealDbRetrofit.create(MealDbApi::class.java)
    }

    private val foodiiDatabase: FoodiiDatabase by lazy {
        FoodiiDatabase.getDatabase(context)
    }

    val foodiiRepository: MealFoodiiRepository by lazy {
        MealFoodiiRepositoryImpl(foodiiApi, foodiiDatabase.mealDao())
    }

    val ingredientRepository: IngredientRepository by lazy {
        IngredientFoodiiRepositoryImpl(foodiiApi, foodiiDatabase.ingredientDao())
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApi, authLocalDataSource)
    }

    val plannerRepository: PlannerRepository by lazy {
        PlannerRepositoryImpl(mealDbApi, foodiiDatabase.plannedMealDao())
    }

    val shakeDetector: ShakeDetector by lazy {
        AndroidShakeDetector(context)
    }

    val cameraManager: CameraManager by lazy {
        AndroidCameraManager(context)
    }

    private val mediaManager: MediaManager by lazy {
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME
        )
        try {
            MediaManager.init(context, config)
        } catch (e: Exception) {
        }
        MediaManager.get()
    }

    private val cloudinaryService: CloudinaryService by lazy {
        CloudinaryServiceImpl(mediaManager)
    }

    val imageRepository: ImageRepository by lazy {
        CloudinaryImageRepositoryImpl(cloudinaryService)
    }

    val mealModule: MealFoodiiModule by lazy {
        MealFoodiiModule(
            mealRepository = foodiiRepository,
            ingredientRepository = ingredientRepository,
            plannerRepository = plannerRepository,
            context = context,
            shakeDetector = shakeDetector,
            cameraManager = cameraManager,
            imageRepository = imageRepository
        )
    }

    val ingredientModule: IngredientFoodiiModule by lazy {
        IngredientFoodiiModule(
            ingredientRepository = ingredientRepository,
            authRepository = authRepository
        )
    }
}
