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
    val instrument: List<String>,
    val genres: List<String>,
    val experienceLevel: String,
    val isInBand: Boolean
)

/**
 * LoginRequest - Data class for user login
 */
data class LoginRequest(
    val email: String,
    val password: String
)

data class UpdateFieldRequest(
    val idUser: Int?,
    val keyField: String,
    val valueField: Any,
)