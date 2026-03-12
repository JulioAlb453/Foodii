package com.example.foodii.core.di

import android.content.Context
import com.example.foodii.feature.apifoodii.ingredient.data.local.dao.IngredientRoomDao
import com.example.foodii.feature.apifoodii.meal.data.local.dao.MealRoomDao
import com.example.foodii.feature.mealdb.data.local.dao.PlannedMealDao
import com.example.foodii.feature.mealdb.data.local.database.FoodiiDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FoodiiDatabase {
        return FoodiiDatabase.getDatabase(context)
    }

    @Provides
    fun providePlannedMealDao(database: FoodiiDatabase): PlannedMealDao {
        return database.plannedMealDao()
    }

    @Provides
    fun provideMealRoomDao(database: FoodiiDatabase): MealRoomDao {
        return database.mealDao()
    }

    @Provides
    fun provideIngredientRoomDao(database: FoodiiDatabase): IngredientRoomDao {
        return database.ingredientDao()
    }
}
