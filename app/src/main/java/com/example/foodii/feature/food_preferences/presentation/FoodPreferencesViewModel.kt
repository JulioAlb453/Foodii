package com.example.foodii.feature.food_preferences.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.auth.domain.repository.AuthRepository
import com.example.foodii.feature.food_preferences.domain.model.NotificationCategory
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class FoodPreferencesUiState(
    val selectedSlugs: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class FoodPreferencesViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodPreferencesUiState())
    val uiState: StateFlow<FoodPreferencesUiState> = _uiState.asStateFlow()

    val categories = NotificationCategory.ALL

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser()?.let { user ->
                _uiState.update { it.copy(
                    selectedSlugs = user.notificationCategoryPreferences?.toSet() ?: emptySet()
                ) }
            }
        }
    }

    fun onCategoryToggled(slug: String) {
        val current = _uiState.value.selectedSlugs
        val updated = if (current.contains(slug)) {
            current - slug
        } else {
            current + slug
        }
        _uiState.update { it.copy(selectedSlugs = updated) }
    }

    fun savePreferences(onDone: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val user = authRepository.getCurrentUser()
            val oldSlugs = user?.notificationCategoryPreferences?.toSet() ?: emptySet()
            val newSlugs = _uiState.value.selectedSlugs

            // 1. Backend PATCH
            val fcmToken = try {
                FirebaseMessaging.getInstance().token.await()
            } catch (e: Exception) {
                null
            }
            
            val result = authRepository.updatePreferences(newSlugs.toList(), fcmToken)

            if (result.isSuccess) {
                // 2. Sync FCM Topics
                syncTopics(oldSlugs, newSlugs)
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
                onDone()
            } else {
                _uiState.update { it.copy(isLoading = false) }
                Log.e("FoodPreferences", "Error saving preferences: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    private fun syncTopics(oldSlugs: Set<String>, newSlugs: Set<String>) {
        val toSubscribe = newSlugs - oldSlugs
        val toUnsubscribe = oldSlugs - newSlugs

        toSubscribe.forEach { slug ->
            FirebaseMessaging.getInstance().subscribeToTopic(slug)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) Log.d("FCM_TOPIC", "Subscribed to $slug")
                }
        }

        toUnsubscribe.forEach { slug ->
            FirebaseMessaging.getInstance().unsubscribeFromTopic(slug)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) Log.d("FCM_TOPIC", "Unsubscribed from $slug")
                }
        }
    }
}
