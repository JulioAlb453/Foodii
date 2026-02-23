package com.example.foodii.feature.mealdb.di

import android.content.Context
import com.example.foodii.feature.mealdb.data.datasource.api.MealDbApi
import com.example.foodii.feature.mealdb.data.datasource.repositories.PlannerRepositoryImpl
import com.example.foodii.feature.mealdb.data.local.dao.PlannedMealDao
import com.example.foodii.feature.mealdb.data.local.database.FoodiiDatabase
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MealDbModule {

    @Provides
    @Singleton
    fun provideMealDbAPI(retrofit: Retrofit): MealDbApi {
        return retrofit.create(MealDbApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFoodiiDatabase(@ApplicationContext context: Context): FoodiiDatabase {
        return FoodiiDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun providePlannedMealDao(database: FoodiiDatabase): PlannedMealDao {
        return database.plannedMealDao()
    }

    @Provides
    @Singleton
    fun providePlannerRepository(
        api: MealDbApi,
        dao: PlannedMealDao
    ): PlannerRepository {
        return PlannerRepositoryImpl(api, dao)
    }
}
