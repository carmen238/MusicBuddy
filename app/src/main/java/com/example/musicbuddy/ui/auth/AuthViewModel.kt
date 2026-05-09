package com.example.musicbuddy.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * AuthViewModel - Gestisce l'autenticazione con Firebase
 * Salva i dati localmente con SharedPreferences
 */
class AuthViewModel : ViewModel() {

    // Istanza di Firebase Authentication
    private val auth: FirebaseAuth = Firebase.auth

    // StateFlow autenticazione
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // StateFlow per i dati dell'utente
    private val _userData = MutableStateFlow<Map<String, String>?>(null)
    val userData: StateFlow<Map<String, String>?> = _userData

    // SharedPreferences
    private var userPreferences: UserPreferences? = null

    /**
     * Inizializza il contesto per SharedPreferences
     */
    fun setContext(context: Context) {
        userPreferences = UserPreferences(context)
    }

    /**
     * Login con email e password
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // Autentica con Firebase
                auth.signInWithEmailAndPassword(email, password).await()

                // Carica i dati salvati
                fetchUserData()

                // Se successo
                _authState.value = AuthState.Authenticated

            } catch (e: Exception) {
                // Se errore
                _authState.value = AuthState.Error(
                    message = when {
                        e.message?.contains("There is no user record") == true ->
                            "Email non registrata"
                        e.message?.contains("The password is invalid") == true ->
                            "Password errata"
                        e.message?.contains("The email address is badly formatted") == true ->
                            "Email non valida"
                        else -> "Errore durante il login: ${e.message}"
                    }
                )
            }
        }
    }

    /**
     * Registrazione con email e password
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

                // Crea l'utente su Firebase
                auth.createUserWithEmailAndPassword(email, password).await()

                // Salva i dati localmente in SharedPreferences
                userPreferences?.saveUserData(name, surname, email, phone)

                // Se successo
                _authState.value = AuthState.Authenticated

            } catch (e: Exception) {
                // Se errore
                _authState.value = AuthState.Error(
                    message = when {
                        e.message?.contains("The email address is already in use") == true ->
                            "Email già registrata"
                        e.message?.contains("The email address is badly formatted") == true ->
                            "Email non valida"
                        e.message?.contains("The given password is invalid") == true ->
                            "Password non valida (minimo 6 caratteri)"
                        else -> "Errore durante la registrazione: ${e.message}"
                    }
                )
            }
        }
    }

    /**
     * Recupera i dati dell'utente da SharedPreferences
     */
    fun fetchUserData() {
        _userData.value = userPreferences?.getUserData()
    }

    /**
     * Logout
     */
    fun logout() {
        auth.signOut()
        userPreferences?.clearUserData()
        _userData.value = null
        _authState.value = AuthState.Unauthenticated
    }

    /**
     * Controlla se l'utente è già loggato
     */
    fun checkAuthState() {
        _authState.value = if (auth.currentUser != null) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }

    /**
     * Pulisce il messaggio di errore
     */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}