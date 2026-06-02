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
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel : ViewModel() {

    private val authApiService = RetrofitClient.getAuthApiService()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _userData = MutableStateFlow<Map<String, Any>?>(null)
    val userData: StateFlow<Map<String, Any>?> = _userData

    private var userPreferences: UserPreferences? = null

    fun setContext(context: Context) {
        userPreferences = UserPreferences(context)
        checkAuthState()
    }

    // -------------------------
    // REGISTER
    // -------------------------

    fun signUp(
        email: String,
        password: String,
        name: String,
        surname: String,
        phone: String,
        instrument: String = "",
        experienceLevel: String = "",
        genre: String = "",
        isInBand: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val request = RegisterRequest(
                    email = email,
                    password = password,
                    name = name,
                    surname = surname,
                    phone = phone
                )

                val response = authApiService.register(request)

                Log.d("AuthViewModel", "✅ Registration successful")

                userPreferences?.saveUserData(
                    name = name,
                    surname = surname,
                    email = email,
                    phone = phone,
                    userId = response.userId,
                    bio = "",
                    instrument = instrument,
                    experienceLevel = experienceLevel,
                    genre = genre,
                    isInBand = isInBand
                )

                _authState.value = AuthState.Authenticated

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val msg = try {
                    Gson().fromJson(errorBody, Map::class.java)["error"]?.toString()
                } catch (ex: Exception) {
                    "Registration failed"
                }
                _authState.value = AuthState.Error(msg ?: "Error")

            } catch (e: IOException) {
                _authState.value = AuthState.Error("Network error")

            } catch (e: Exception) {
                _authState.value = AuthState.Error("Unexpected error")
            }
        }
    }

    // -------------------------
    // LOGIN
    // -------------------------

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val request = LoginRequest(email, password)
                val response = authApiService.login(request)

                val user = response.user

                userPreferences?.saveAuthToken(response.token)

                userPreferences?.saveUserData(
                    name = user.name,
                    surname = user.surname,
                    email = user.email,
                    phone = user.phone ?: "",
                    userId = user.id,
                    bio = user.bio ?: "",
                    instrument = user.instrument ?: "",
                    experienceLevel = user.experienceLevel ?: "",
                    genre = user.genre ?: "",
                    isInBand = user.isInBand ?: false,
                    photoUrl = user.photo_url ?: ""
                )

                _userData.value = mapOf(
                    "userId" to user.id,
                    "name" to user.name,
                    "surname" to user.surname,
                    "email" to user.email,
                    "phone" to (user.phone ?: ""),
                    "bio" to (user.bio ?: ""),
                    "instrument" to (user.instrument ?: ""),
                    "genre" to (user.genre ?: ""),
                    "experienceLevel" to (user.experienceLevel ?: ""),
                    "isInBand" to (user.isInBand ?: false),
                    "photo_url" to (user.photo_url ?: "")
                )

                _authState.value = AuthState.Authenticated

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val msg = try {
                    Gson().fromJson(errorBody, Map::class.java)["error"]?.toString()
                } catch (ex: Exception) {
                    "Login failed"
                }
                _authState.value = AuthState.Error(msg ?: "Error")

            } catch (e: IOException) {
                _authState.value = AuthState.Error("Network error")

            } catch (e: Exception) {
                _authState.value = AuthState.Error("Unexpected error")
            }
        }
    }

    // -------------------------
    // FETCH LOCAL
    // -------------------------

    fun fetchUserData() {
        try {
            _userData.value = userPreferences?.getUserData()
            Log.d("AuthViewModel", "User data loaded")
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error fetching user data")
        }
    }

    // -------------------------
    // UPDATE FIELD
    // -------------------------

    fun updateUserField(id: String?, field: String, value: String) {
        viewModelScope.launch {
            try {
                if (id == null) {
                    _authState.value = AuthState.Error("User ID not found")
                    return@launch
                }

                val request = UpdateFieldRequest(
                    idUser = id,
                    keyField = field,
                    valueField = value
                )

                authApiService.updateFieldUser(request)

                userPreferences?.saveUserField(field, value)

                fetchUserData()

                _authState.value = AuthState.Authenticated

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val msg = try {
                    Gson().fromJson(errorBody, Map::class.java)["error"]?.toString()
                } catch (ex: Exception) {
                    "Update failed"
                }
                _authState.value = AuthState.Error(msg ?: "Error")

            } catch (e: IOException) {
                _authState.value = AuthState.Error("Network error")

            } catch (e: Exception) {
                _authState.value = AuthState.Error("Update failed: ${e.message}")
            }
        }
    }

    // -------------------------
    // LOGOUT
    // -------------------------


    // -------------------------
    // AUTH CHECK
    // -------------------------

    fun checkAuthState() {
        val token = userPreferences?.getAuthToken()

        _authState.value = if (!token.isNullOrEmpty()) {
            fetchUserData()
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                // 1. Cancella il token dalle SharedPreferences¬
                userPreferences?.clearAll()
                _userData.value = null
                _authState.value = AuthState.Unauthenticated
                // 3. Log per debug
                Log.d("AuthViewModel", "Logout completato")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Errore durante logout: ${e.message}")
            }
        }
    }
}