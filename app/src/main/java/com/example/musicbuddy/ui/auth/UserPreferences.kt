package com.example.musicbuddy.ui.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * UserPreferences - Manages user data and JWT token storage
 * Uses SharedPreferences for local persistence
 */
class UserPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NAME = "name"
        private const val KEY_SURNAME = "surname"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phone"
        private const val KEY_BIO = "bio"
        private const val KEY_RATING = "rating"
    }

    /**
     * Save JWT authentication token
     */
    fun saveAuthToken(token: String) {
        sharedPreferences.edit().apply {
            putString(KEY_AUTH_TOKEN, token)
            apply()
        }
        println("✅ Auth token saved")
    }

    /**
     * Get JWT authentication token
     */
    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Clear JWT authentication token
     */
    fun clearAuthToken() {
        sharedPreferences.edit().apply {
            remove(KEY_AUTH_TOKEN)
            apply()
        }
        println("✅ Auth token cleared")
    }

    /**
     * Check if user is logged in (has valid token)
     */
    fun isUserLoggedIn(): Boolean {
        return !getAuthToken().isNullOrEmpty()
    }

    /**
     * Save user data
     */
    fun saveUserData(
        name: String,
        surname: String,
        email: String,
        phone: String,
        userId: Int,
        bio: String?
    ) {
        sharedPreferences.edit().apply {
            putString(KEY_NAME, name)
            putString(KEY_SURNAME, surname)
            putString(KEY_EMAIL, email)
            putString(KEY_PHONE, phone)
            putString(KEY_BIO, bio)
            putInt(KEY_RATING, 0)
            putString(KEY_USER_ID, userId.toString())
            apply()
        }
        println("✅ User data saved: $name $surname")
    }

    /**
     * Get user data
     */
    fun getUserData(): Map<String, String> {
        return mapOf(
            "name" to (sharedPreferences.getString(KEY_NAME, "Nome") ?: "Nome"),
            "surname" to (sharedPreferences.getString(KEY_SURNAME, "Cognome") ?: "Cognome"),
            "email" to (sharedPreferences.getString(KEY_EMAIL, "Email") ?: "Email"),
            "phone" to (sharedPreferences.getString(KEY_PHONE, "Telefono") ?: "Telefono"),
            "bio" to (sharedPreferences.getString(KEY_BIO, "") ?: ""),
            "userId" to (sharedPreferences.getString(KEY_USER_ID, "0") ?: "0")
        )
    }

    /**
     * Update a single user field
     */
    fun saveUserField(field: String, value: String) {
        sharedPreferences.edit().apply {
            putString(field, value)
            apply()
        }
        println("✅ User field updated: $field = $value")
    }

    /**
     * Clear all user data
     */
    fun clearUserData() {
        sharedPreferences.edit().apply {
            remove(KEY_NAME)
            remove(KEY_SURNAME)
            remove(KEY_EMAIL)
            remove(KEY_PHONE)
            remove(KEY_BIO)
            remove(KEY_RATING)
            apply()
        }
        println("✅ User data cleared")
    }

    /**
     * Clear all data (logout)
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
        println("✅ All preferences cleared")
    }
}