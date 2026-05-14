package com.example.musicbuddy.data.models

/**
 * RegisterRequest - Data class for user registration
 */
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
    val phone: String
)

/**
 * LoginRequest - Data class for user login
 */
data class LoginRequest(
    val email: String,
    val password: String
)