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
        Log.e("AWS_AUTH", "--- Iniciando Interceptor de Red ---")
        
        val user = runBlocking {
            localDataSource.getUser().firstOrNull()
        }
        val token = user?.token

        val requestBuilder = chain.request().newBuilder()
        
        if (!token.isNullOrEmpty()) {
            // Express suele requerir el prefijo "Bearer "
            val formattedToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            Log.e("AWS_AUTH", "TOKEN ENCONTRADO: ${formattedToken.take(20)}...")
            requestBuilder.header("Authorization", formattedToken)
        } else {
            Log.e("AWS_AUTH", "ERROR: No hay token en DataStore. ¿Usuario logueado?")
        }

        val request = requestBuilder.build()
        Log.e("AWS_AUTH", "Enviando petición a: ${request.url}")

        return chain.proceed(request)
    }
}
