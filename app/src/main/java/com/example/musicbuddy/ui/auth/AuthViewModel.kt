package com.example.musicbuddy.ui.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicbuddy.data.models.DeleteUserRequest
import com.example.musicbuddy.data.models.GenreInfoField
import com.example.musicbuddy.data.models.InstrumentInfoField
import com.example.musicbuddy.data.models.LoginRequest
import com.example.musicbuddy.data.models.RegisterRequest
import com.example.musicbuddy.data.models.UpdateFieldRequest
import com.example.musicbuddy.data.models.UserInfos
import com.example.musicbuddy.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import kotlin.String
import kotlin.collections.List

class AuthViewModel : ViewModel() {

    private val authApiService = RetrofitClient.getAuthApiService()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _userData = MutableStateFlow<Map<String, Any>?>(null)
    val userData: StateFlow<Map<String, Any>?> = _userData

    private val _allUsersInfos = MutableStateFlow<List<UserInfos>>(emptyList())
    val allUsersInfos: StateFlow<List<UserInfos>> = _allUsersInfos
    //^^^ non servono

    private val _genreStatsState = MutableStateFlow<List<GenreInfoField>>(emptyList())
    val genreStatsState: StateFlow<List<GenreInfoField>> = _genreStatsState

    private val _instrumentsStatsState = MutableStateFlow<List<InstrumentInfoField>>(emptyList())
    val instrumentsStatsState: StateFlow<List<InstrumentInfoField>> = _instrumentsStatsState

    private val _totNumUsersState = MutableStateFlow<Int>(0)
    val totNumUsersState: StateFlow<Int> = _totNumUsersState

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
        instrument: String,
        experienceLevel: String,
        genre: String,
        isInBand: Boolean = false,
        onNavigateToStart: () -> Unit  // ✅ Aggiungi questo parametro
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
                    bio = "",
                    instrument = instrument,
                    experienceLevel = experienceLevel,
                    genre = genre,
                    isInBand = isInBand,
                    photoUrl = ""   //inserito per non far crushare il server
                )

                val response = authApiService.register(request)

                Log.d("AuthViewModel", "✅ Registration successful")

                // ✅ Salva il token
                userPreferences?.saveAuthToken(response.token)

                // ✅ Salva l'ID utente
                userPreferences?.saveUserId(response.userId)

                // ✅ Salva i dati utente
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

                // ✅ Chiama il callback per navigare
                onNavigateToStart()

            } catch (e: HttpException) {
                onNavigateToStart()
                val errorBody = e.response()?.errorBody()?.string()
                val msg = try {
                    Gson().fromJson(errorBody, Map::class.java)["error"]?.toString()

                } catch (ex: Exception) {
                    "Registration failed"

                }
                _authState.value = AuthState.Error(msg ?: "Error")
                onNavigateToStart()
            } catch (e: IOException) {
                _authState.value = AuthState.Error("Network error")
                onNavigateToStart()
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Unexpected error")
                onNavigateToStart()
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
                println("loggatoo" + response.user)

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
                Log.e("AuthViewModel", "Error: "+e.message)

            } catch (e: Exception) {
                _authState.value = AuthState.Error("Unexpected error")
                Log.e("AuthViewModel", "Error: "+e.message)
            }
        }
    }

    // -------------------------
    // FETCH LOCAL
    // -------------------------

    fun fetchUserData() {
        try {
            _userData.value = userPreferences?.getUserData()
            println("fetchUserData" + _userData.value)
            Log.d("AuthViewModel", "User data loaded", )
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

    fun getAllUsersInfos() {
        viewModelScope.launch {
            try {
                val response = authApiService.getAllUsersInfos()

                if (response.success) {
                    _allUsersInfos.value = response.data
                } else {
                    println("API returned success = false")
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val msg = try {
                    Gson().fromJson(errorBody, Map::class.java)["error"]?.toString()
                } catch (ex: Exception) {
                    "Info retrieval failed"
                }
                Log.e("AuthViewModel", "Error retrieving users infos: " + e.message.toString())

            } catch (e: IOException) {
                Log.e("AuthViewModel", "Error retrieving users infos: " + e.message.toString())

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error retrieving users infos: " + e.message.toString())
            }
        }
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

    // -------------------------
    // LOGOUT
    // -------------------------
    fun logout() {
        viewModelScope.launch {
            try {
                // 1. Cancella il token dalle SharedPreferences¬
                userPreferences?.clearAll()
                userPreferences?.clearAuthToken()
                userPreferences?.clearUserData()
                _userData.value = null
                _authState.value = AuthState.Unauthenticated
                // 3. Log per debug
                Log.d("AuthViewModel", "Logout completato")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Errore durante logout: ${e.message}")
            }
        }
    }

    // -------------------------
    // DELETE ACCOUNT
    // -------------------------
    fun deleteAccount() {
        viewModelScope.launch {
            try {
                val deleteReq = DeleteUserRequest(userPreferences?.getUserData()["userId"] as Int)
                println("QUI "+deleteReq.userId)
                val response = authApiService.deleteUser(deleteReq)

                if(!response.success) Log.e("AuthViewModel", "Error during account deletion on server side: ${response.message}")

                userPreferences?.clearAll()
                userPreferences?.clearAuthToken()
                userPreferences?.clearUserData()
                _userData.value = null
                _authState.value = AuthState.Unauthenticated

                // 3. Log per debug
                Log.d("AuthViewModel", "Account deleted")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error during account deletion: ${e.message}")
            }
        }
    }

    // -------------------------
    // GET GENRES STATS
    // -------------------------
    fun getGenresStats() {
        viewModelScope.launch {
            try {
                val response = authApiService.getGenresStats()

                if (response.success) {
                    _genreStatsState.value = response.data
                } else {
                    Log.e("AuthViewModel", "Error retrieving genres stats: success = false")
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val msg = try {
                    Gson().fromJson(errorBody, Map::class.java)["error"]?.toString()
                } catch (ex: Exception) {
                    "Info retrieval failed"
                }
                Log.e("AuthViewModel", "Error retrieving genres stats: " + e.message.toString())

            } catch (e: IOException) {
                Log.e("AuthViewModel", "Error retrieving genres stats: " + e.message.toString())

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error retrieving genres stats: " + e.message.toString())
            }
        }
    }

    // -------------------------
    // GET INSTRUMENTS STATS
    // -------------------------
    fun getInstrumentsStats() {
        viewModelScope.launch {
            try {
                val response = authApiService.getInstrumentsStats()

                if (response.success) {
                    _instrumentsStatsState.value = response.data
                } else {
                    Log.e("AuthViewModel", "Error retrieving instruments stats: success = false")
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val msg = try {
                    Gson().fromJson(errorBody, Map::class.java)["error"]?.toString()
                } catch (ex: Exception) {
                    "Info retrieval failed"
                }
                Log.e("AuthViewModel", "Error retrieving instruments stats: " + e.message.toString())

            } catch (e: IOException) {
                Log.e("AuthViewModel", "Error retrieving instruments stats: " + e.message.toString())

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error retrieving instruments stats: " + e.message.toString())
            }
        }
    }

    // -------------------------
    // GET TOTAL NUMBER OF USERS
    // -------------------------
    fun getTotNumUsers() {
        viewModelScope.launch {
            try {
                val response = authApiService.getTotNumUsers()

                if (response.success) {
                    _totNumUsersState.value = response.totNumUsers
                } else {
                    Log.e("AuthViewModel", "Error retrieving total number of users: success = false")
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val msg = try {
                    Gson().fromJson(errorBody, Map::class.java)["error"]?.toString()
                } catch (ex: Exception) {
                    "Info retrieval failed"
                }
                Log.e("AuthViewModel", "Error retrieving total number of users: " + e.message.toString())

            } catch (e: IOException) {
                Log.e("AuthViewModel", "Error retrieving total number of users: " + e.message.toString())

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error retrieving total number of users: " + e.message.toString())
            }
        }
    }
}