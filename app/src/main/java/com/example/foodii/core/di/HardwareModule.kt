package com.example.foodii.core.di

import android.content.Context
import com.example.foodii.core.hardware.data.AndroidCameraManager
import com.example.foodii.core.hardware.data.AndroidShakeDetector
import com.example.foodii.core.hardware.domain.CameraManager
import com.example.foodii.core.hardware.domain.ShakeDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HardwareModule {

    @Provides
    @Singleton
    fun provideShakeDetector(@ApplicationContext context: Context): ShakeDetector {
        return AndroidShakeDetector(context)
    }

    @Provides
    @Singleton
    fun provideCameraManager(@ApplicationContext context: Context): CameraManager {
        return AndroidCameraManager(context)
    }
}
