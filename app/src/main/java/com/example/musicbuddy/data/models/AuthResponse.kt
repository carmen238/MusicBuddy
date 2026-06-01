package com.example.musicbuddy.data.models

/**
 * RegisterResponse - Response from registration endpoint
 */
data class RegisterResponse(
    val message: String,
    val userId: Int
)

/**
 * LoginResponse - Response from login endpoint
 */
data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)

/**
 * User - User data returned from backend
 */
data class User(
    val id: Int,
    val email: String,
    val name: String,
    val surname: String,
    val phone: String? = null,
    val bio: String? = null,
    val instrument: List<String>,
    val genres: List<String>,
    val experienceLevel: String,
    val isInBand: Boolean
)
/**
 * ErrorResponse - Error response from backend
 */
data class ErrorResponse(
    val error: String
)

data class UpdateFieldResponse(
    val message: String
)
