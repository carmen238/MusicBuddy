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
import org.json.JSONArray
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
    // HELPERS
    // -------------------------

    private fun parseJsonArray(value: List<String>): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        val array = JSONArray(value)
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
        return list
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
        instrument: String,
        experienceLevel: String,
        favoriteGenre: String,
        isInBand: Boolean
    ) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val request = RegisterRequest(
                    email = email,
                    password = password,
                    name = name,
                    surname = surname,
                    phone = phone,

                    instrument = listOf(instrument),
                    genres = listOf(favoriteGenre),

                    experienceLevel = experienceLevel,
                    isInBand = isInBand
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
                    instrument = listOf(instrument),
                    experienceLevel = experienceLevel,
                    favoriteGenre = listOf(favoriteGenre),
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

                val instrumentList = parseJsonArray(user.instrument)
                val genreList = parseJsonArray(user.genres)

                userPreferences?.saveAuthToken(response.token)

                userPreferences?.saveUserData(
                    name = user.name,
                    surname = user.surname,
                    email = user.email,
                    phone = user.phone ?: "",
                    userId = user.id,
                    bio = user.bio ?: "",
                    instrument = instrumentList,
                    experienceLevel = user.experienceLevel ?: "",
                    favoriteGenre = genreList,
                    isInBand = user.isInBand ?: false
                )

                _userData.value = mapOf(
                    "userId" to user.id,
                    "name" to user.name,
                    "surname" to user.surname,
                    "email" to user.email,
                    "phone" to (user.phone ?: ""),
                    "bio" to (user.bio ?: ""),
                    "instrument" to instrumentList,
                    "favoriteGenre" to genreList,
                    "experienceLevel" to (user.experienceLevel ?: ""),
                    "isInBand" to (user.isInBand ?: false)
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

    fun updateUserField(id: Int?, field: String, value: Any) {
        viewModelScope.launch {
            try {
                val request = UpdateFieldRequest(
                    idUser = id,
                    keyField = field,
                    valueField = value
                )

                authApiService.updateFieldUser(request)

                userPreferences?.saveUserField(field, value)

                fetchUserData()

            } catch (e: Exception) {
                _authState.value = AuthState.Error("Update failed")
            }
        }
    }

    // -------------------------
    // LOGOUT
    // -------------------------

    fun logout() {
        userPreferences?.clearAll()
        _userData.value = null
        _authState.value = AuthState.Unauthenticated
    }

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
}