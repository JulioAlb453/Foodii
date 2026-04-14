package com.example.foodii.feature.apifoodii.meal.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodii.feature.apifoodii.meal.domain.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val url: String) : UploadState()
    data class Error(val message: String) : UploadState()
}

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val repository: ImageRepository
) : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState = _uploadState.asStateFlow()

    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            val result = repository.uploadImage(uri)
            result.onSuccess { url ->
                _uploadState.value = UploadState.Success(url)
            }.onFailure { exception ->
                _uploadState.value = UploadState.Error(exception.message ?: "Error desconocido")
            }
        }
    }

    fun resetState() {
        _uploadState.value = UploadState.Idle
    }
}
