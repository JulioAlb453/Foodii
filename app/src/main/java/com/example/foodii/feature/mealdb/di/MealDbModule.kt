package com.example.foodii.feature.mealdb.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MealDbModule {
    // All database-related bindings moved to core.di.DatabaseModule to avoid duplication.
}
