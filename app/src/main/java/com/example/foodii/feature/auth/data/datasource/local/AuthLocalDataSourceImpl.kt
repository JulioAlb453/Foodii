package com.example.foodii.feature.auth.data.datasource.local

import android.content.Context
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
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = user.id
            preferences[USERNAME] = user.username
            user.token?.let { preferences[TOKEN] = it }
        }
    }

    override fun getUser(): Flow<User?> {
        return context.dataStore.data.map { preferences ->
            val id = preferences[USER_ID] ?: return@map null
            val username = preferences[USERNAME] ?: ""
            val token = preferences[TOKEN]
            User(id, username, token)
        }
    }

    override suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USERNAME)
            preferences.remove(TOKEN)
        }
    }
}
