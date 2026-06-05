package com.example.musicbuddy.data.models

/**
 * RegisterResponse - Response from registration endpoint
 */
data class RegisterResponse(
    val message: String,
    val userId: Int,
    val token: String
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
    val instrument: String? = null,           // ✅ String singolo
    val experienceLevel: String? = null,
    val genre: String? = null,        // ✅ String singolo
    val isInBand: Boolean? = false,
    val photo_url: String? = null
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

data class UserInfos(
    val id: Int,
    val instrument: String? = null,           // ✅ String singolo
    val experienceLevel: String? = null,
    val genre: String? = null,        // ✅ String singolo
    val isInBand: Boolean? = false,
    val rating: Int
)

data class GetAllUsersResponse<T>(
    val success: Boolean,
    val data: T
)