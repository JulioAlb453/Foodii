package com.example.foodii.core.hardware.domain

interface ShakeDetector {
    fun startListening(onShake: () -> Unit)
    fun stopListening()
}
