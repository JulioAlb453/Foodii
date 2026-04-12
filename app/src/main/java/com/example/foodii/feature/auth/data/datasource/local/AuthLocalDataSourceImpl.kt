package com.example.foodii.feature.auth.data.datasource.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodii.feature.auth.domain.entity.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthLocalDataSourceImpl(private val context: Context) : AuthLocalDataSource {

    private val gson = Gson()

    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val TOKEN = stringPreferencesKey("token")
        private val PREFERENCES = stringPreferencesKey("notification_category_preferences")
    }

    override suspend fun saveUser(user: User) {
        try {
            context.dataStore.edit { preferences ->
                preferences[USER_ID] = user.id
                preferences[USERNAME] = user.username
                user.token?.let { preferences[TOKEN] = it }
                
                // Guardar las preferencias como JSON string
                user.notificationCategoryPreferences?.let {
                    preferences[PREFERENCES] = gson.toJson(it)
                }
            }
            Log.d("AUTH_LOCAL", "Usuario guardado exitosamente: ${user.username}")
        } catch (e: Exception) {
            Log.e("AUTH_LOCAL", "Error al guardar usuario: ${e.message}")
        }
    }

    override fun getUser(): Flow<User?> {
        return context.dataStore.data.map { preferences ->
            val id = preferences[USER_ID] ?: return@map null
            val token = preferences[TOKEN] ?: return@map null
            val username = preferences[USERNAME] ?: ""
            
            // Recuperar las preferencias del JSON
            val prefsJson = preferences[PREFERENCES]
            val categoryPrefs = if (prefsJson != null) {
                try {
                    gson.fromJson(prefsJson, Array<String>::class.java).toList()
                } catch (e: Exception) {
                    null
                }
            } else null
            
            User(
                id = id,
                username = username,
                token = token,
                notificationCategoryPreferences = categoryPrefs
            )
        }
    }

    override suspend fun clearUser() {
        context.dataStore.edit { it.clear() }
    }
}
