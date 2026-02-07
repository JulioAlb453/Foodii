package com.example.foodii.feature.auth.data.datasource.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodii.feature.auth.domain.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthLocalDataSourceImpl(private val context: Context) : AuthLocalDataSource {

    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val TOKEN = stringPreferencesKey("token")
    }

    override suspend fun saveUser(user: User) {
        try {
            context.dataStore.edit { preferences ->
                preferences[USER_ID] = user.id
                preferences[USERNAME] = user.username
                user.token?.let { 
                    preferences[TOKEN] = it 
                    Log.d("AUTH_LOCAL", "Token guardado correctamente: ${it.take(10)}...")
                }
            }
        } catch (e: Exception) {
            Log.e("AUTH_LOCAL", "Error al guardar usuario en DataStore: ${e.message}")
        }
    }

    override fun getUser(): Flow<User?> {
        return context.dataStore.data.map { preferences ->
            val id = preferences[USER_ID]
            val token = preferences[TOKEN]
            
            if (id == null || token == null) {
                Log.w("AUTH_LOCAL", "getUser: No se encontró sesión (id=$id, token=${token != null})")
                return@map null
            }
            
            User(id, preferences[USERNAME] ?: "", token)
        }
    }

    override suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
        Log.d("AUTH_LOCAL", "Sesión eliminada de DataStore")
    }
}
