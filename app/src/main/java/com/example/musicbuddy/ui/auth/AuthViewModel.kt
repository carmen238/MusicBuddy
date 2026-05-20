package com.example.musicbuddy.ui.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicbuddy.data.models.LoginRequest
import com.example.musicbuddy.data.models.RegisterRequest
import com.example.musicbuddy.data.models.UpdateFieldRequest
import com.example.musicbuddy.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.Int

/**
 * AuthViewModel - Handles authentication with Node.js backend
 * Replaces Firebase Auth with REST API calls
 */
class AuthViewModel : ViewModel() {

    private val authApiService = RetrofitClient.getAuthApiService()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _userData = MutableStateFlow<Map<String, String>?>(null)
    val userData: StateFlow<Map<String, String>?> = _userData

    private var userPreferences: UserPreferences? = null

    fun setContext(context: Context) {
        userPreferences = UserPreferences(context)
        checkAuthState()
    }

    /**
     * Register a new user with the backend
     */
    fun signUp(
        email: String,
        password: String,
        name: String,
        surname: String,
        phone: String
    ) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d("AuthViewModel", "Registration attempt for: $email")

                // Create registration request
                val request = RegisterRequest(
                    email = email,
                    password = password,
                    name = name,
                    surname = surname,
                    phone = phone
                )

                // Call backend API
                val response = authApiService.register(request)

                Log.d("AuthViewModel", "✅ Registration successful: ${response.message}")

                // Save user data locally
                userPreferences?.saveUserData(name, surname, email, phone)

                // Update state
                _authState.value = AuthState.Authenticated

            } catch (e: retrofit2.HttpException) {
                // Handle HTTP errors (400, 401, 500, etc.)
                Log.e("AuthViewModel", "HTTP Error: ${e.code()} - ${e.message()}")

                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    val gson = Gson()
                    val errorResponse = gson.fromJson(errorBody, Map::class.java)
                    errorResponse["error"]?.toString() ?: "Registration failed"
                } catch (ex: Exception) {
                    "Registration failed: ${e.message()}"
                }

                _authState.value = AuthState.Error(message = errorMessage)

            } catch (e: IOException) {
                // Handle network errors
                Log.e("AuthViewModel", "Network error: ${e.message}")
                _authState.value = AuthState.Error(
                    message = "Network error. Check your connection and backend URL."
                )

            } catch (e: Exception) {
                // Handle unknown errors
                Log.e("AuthViewModel", "Unexpected error: ${e.message}")
                _authState.value = AuthState.Error(
                    message = "Registration failed: ${e.message}"
                )
            }
        }
    }

    /**
     * Login user with the backend
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d("AuthViewModel", "Login attempt for: $email")

                // Create login request
                val request = LoginRequest(
                    email = email,
                    password = password
                )

                // Call backend API
                val response = authApiService.login(request)

                Log.d("AuthViewModel", "✅ Login successful: ${response.message}")

                // Save JWT token
                userPreferences?.saveAuthToken(response.token)

                // Save user data
                val user = response.user
                userPreferences?.saveUserData(
                    user.name,
                    user.surname,
                    user.email,
                    user.phone ?: ""
                )

                // Update userData state
                _userData.value = mapOf(
                    "id" to user.id.toString(),
                    "name" to user.name,
                    "surname" to user.surname,
                    "email" to user.email,
                    "phone" to (user.phone ?: ""),
                    "bio" to (user.bio ?: "")
                )

                // Update auth state
                _authState.value = AuthState.Authenticated

            } catch (e: retrofit2.HttpException) {
                // Handle HTTP errors
                Log.e("AuthViewModel", "HTTP Error: ${e.code()} - ${e.message()}")

                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    val gson = Gson()
                    val errorResponse = gson.fromJson(errorBody, Map::class.java)
                    errorResponse["error"]?.toString() ?: "Login failed"
                } catch (ex: Exception) {
                    "Login failed: ${e.message()}"
                }

                _authState.value = AuthState.Error(message = errorMessage)

            } catch (e: IOException) {
                // Handle network errors
                Log.e("AuthViewModel", "Network error: ${e.message}")
                _authState.value = AuthState.Error(
                    message = "Network error. Check your connection and backend URL."
                )

            } catch (e: Exception) {
                // Handle unknown errors
                Log.e("AuthViewModel", "Unexpected error: ${e.message}")
                _authState.value = AuthState.Error(
                    message = "Login failed: ${e.message}"
                )
            }
        }
    }

    /**
     * Fetch user data from local storage
     */
    fun fetchUserData() {
        try {
            _userData.value = userPreferences?.getUserData()
            Log.d("AuthViewModel", "User data loaded: ${_userData.value}")
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error fetching user data: ${e.message}")
        }
    }

    /**
     * Update user field with the backend
     */
    fun updateUserField(id: Int, field: String, value: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d("AuthViewModel", "Update user field for: $id")

                // Create update request
                val request = UpdateFieldRequest(
                    idUser = id,
                    keyField = field,
                    valueField = value
                )

                // Call backend API
                val response = authApiService.updateFieldUser(request = request)

                Log.d("AuthViewModel", "✅ Update of $field successful: ${response.message}")

                // Update local preferences
                userPreferences?.saveUserField(field, value)

                // Fetch updated data
                fetchUserData()

                // Reset state to Authenticated (IMPORTANTE!)
                _authState.value = AuthState.Authenticated

            } catch (e: retrofit2.HttpException) {
                Log.e("AuthViewModel", "HTTP Error: ${e.code()} - ${e.message()}")

                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    val gson = Gson()
                    val errorResponse = gson.fromJson(errorBody, Map::class.java)
                    errorResponse["error"]?.toString() ?: "Update failed"
                } catch (ex: Exception) {
                    "Update failed: ${e.message()}"
                }

                _authState.value = AuthState.Error(message = errorMessage)

            } catch (e: IOException) {
                Log.e("AuthViewModel", "Network error: ${e.message}")
                _authState.value = AuthState.Error(
                    message = "Network error. Check your connection and backend URL."
                )

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Unexpected error: ${e.message}")
                _authState.value = AuthState.Error(
                    message = "Update failed: ${e}"
                )
            }
        }
    }

    /**
     * Logout user
     */
    fun logout() {
        userPreferences?.clearUserData()
        userPreferences?.clearAuthToken()
        _userData.value = null
        _authState.value = AuthState.Unauthenticated
        Log.d("AuthViewModel", "User logged out")
    }

    /**
     * Check if user is already authenticated
     */
    fun checkAuthState() {
        val token = userPreferences?.getAuthToken()
        _authState.value = if (!token.isNullOrEmpty()) {
            fetchUserData()
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
        Log.d("AuthViewModel", "Auth state checked: ${_authState.value}")
    }

    /**
     * Clear error message
     */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}