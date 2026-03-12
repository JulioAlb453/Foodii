package com.example.foodii.core.di

import android.content.Context
import com.example.foodii.BuildConfig
import com.example.foodii.core.network.AuthInterceptor
import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSourceImpl
import com.example.foodii.feature.mealdb.data.datasource.api.MealDbApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideAuthLocalDataSource(@ApplicationContext context: Context): AuthLocalDataSource {
        return AuthLocalDataSourceImpl(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(authLocalDataSource: AuthLocalDataSource): AuthInterceptor {
        return AuthInterceptor(authLocalDataSource)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @FoodiiRetrofit
    fun provideFoodiiRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.FOODII_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @MealDbRetrofit
    fun provideMealDbRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFoodiiApi(@FoodiiRetrofit retrofit: Retrofit): FoodiiAPI {
        return retrofit.create(FoodiiAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideMealDbApi(@MealDbRetrofit retrofit: Retrofit): MealDbApi {
        return retrofit.create(MealDbApi::class.java)
    }
}

@javax.inject.Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FoodiiRetrofit

@javax.inject.Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MealDbRetrofit
