package com.example.foodii.core.network

import android.util.Log
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val localDataSource: AuthLocalDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val user = runBlocking {
            localDataSource.getUser().firstOrNull()
        }

        val token = user?.token?.trim()?.replace("\"", "")

        val requestBuilder = chain.request().newBuilder()
        
        if (!token.isNullOrEmpty()) {
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            Log.d("AWS_AUTH", "Token válido aplicado a: ${chain.request().url.encodedPath}")
            requestBuilder.header("Authorization", formattedToken)
        } else {
            Log.e("AWS_AUTH", "ALERTA: Sin token para: ${chain.request().url}")
        }

        return chain.proceed(requestBuilder.build())
    }
}
