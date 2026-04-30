package com.example.musicbuddy.ui.auth

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
 */
class AuthViewModel : ViewModel() {

    // Istanza di Firebase Authentication
    private val auth: FirebaseAuth = Firebase.auth

    // StateFlow autenticazione
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    /**
     * Login con email e password
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // Chiama Firebase Authentication
                auth.signInWithEmailAndPassword(email, password).await()

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
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // Chiama Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password).await()

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
     * Logout
     */
    fun logout() {
        auth.signOut()
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