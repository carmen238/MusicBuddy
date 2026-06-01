package com.example.musicbuddy.data.models

/**
 * RegisterRequest - Data class for user registration
 */
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
    val phone: String,
    val instrument: String = "",           // ✅ String singolo
    val experienceLevel: String = "",
    val genre: String = "",        // ✅ String singolo
    val isInBand: Boolean = false
)

/**
 * LoginRequest - Data class for user login
 */
data class LoginRequest(
    val email: String,
    val password: String
)

data class UpdateFieldRequest(
    val idUser: String,
    val keyField: String,
    val valueField: String  // ✅ Cambia da Any a String
)