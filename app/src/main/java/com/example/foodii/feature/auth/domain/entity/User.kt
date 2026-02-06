package com.example.foodii.feature.auth.domain.entity

data class User(
    val id: String,
    val username: String,
    val token: String? = null // Token para sesiones seguras
)
