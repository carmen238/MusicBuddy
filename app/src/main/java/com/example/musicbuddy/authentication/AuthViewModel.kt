package com.example.musicbuddy.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * AuthViewModel - Gestisce la logica di autenticazione
 * Usa Firebase Authentication per login, registrazione e logout
 * Espone lo stato di autenticazione tramite StateFlow
 */
class AuthViewModel : ViewModel() {

    // Istanza di Firebase Authentication
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // StateFlow privato per lo stato di autenticazione
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)

    // StateFlow pubblico (read-only) per osservare lo stato
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Inizializza il ViewModel controllando se l'utente è già autenticato
     */
    init {
        checkAuthState()
    }

    /**
     * Controlla se l'utente è già autenticato
     * Usato all'avvio dell'app per ripristinare la sessione
     */
    fun checkAuthState() {
        val currentUser = auth.currentUser
        _authState.value = if (currentUser != null) {
            AuthState.Authenticated(currentUser)
        } else {
            AuthState.Unauthenticated
        }
    }

    /**
     * Registra un nuovo utente con email e password
     * @param email Email dell'utente
     * @param password Password dell'utente
     */
    fun signUp(email: String, password: String) {
        // Validazione input
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email e password non possono essere vuoti")
            return
        }

        // Imposta lo stato a Loading
        _authState.value = AuthState.Loading

        // Esegue la registrazione in background
        viewModelScope.launch {
            try {
                // Crea un nuovo utente con email e password
                val result = auth.createUserWithEmailAndPassword(email, password).await()

                // Se la registrazione ha successo, imposta lo stato a Authenticated
                result.user?.let {
                    _authState.value = AuthState.Authenticated(it)
                } ?: run {
                    _authState.value = AuthState.Error("Errore durante la registrazione")
                }
            } catch (e: FirebaseAuthUserCollisionException) {
                // Email già registrata
                _authState.value = AuthState.Error("Questa email è già registrata")
            } catch (e: IllegalArgumentException) {
                // Email non valida
                _authState.value = AuthState.Error("Email non valida")
            } catch (e: Exception) {
                // Errore generico
                _authState.value = AuthState.Error("Errore durante la registrazione: ${e.message}")
            }
        }
    }

    /**
     * Effettua il login con email e password
     * @param email Email dell'utente
     * @param password Password dell'utente
     */
    fun login(email: String, password: String) {
        // Validazione input
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email e password non possono essere vuoti")
            return
        }

        // Imposta lo stato a Loading
        _authState.value = AuthState.Loading

        // Esegue il login in background
        viewModelScope.launch {
            try {
                // Autentica l'utente con email e password
                val result = auth.signInWithEmailAndPassword(email, password).await()

                // Se il login ha successo, imposta lo stato a Authenticated
                result.user?.let {
                    _authState.value = AuthState.Authenticated(it)
                } ?: run {
                    _authState.value = AuthState.Error("Errore durante il login")
                }
            } catch (e: FirebaseAuthInvalidUserException) {
                // Utente non trovato
                _authState.value = AuthState.Error("Utente non trovato")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                // Password errata
                _authState.value = AuthState.Error("Email o password errata")
            } catch (e: Exception) {
                // Errore generico
                _authState.value = AuthState.Error("Errore durante il login: ${e.message}")
            }
        }
    }

    /**
     * Effettua il logout dell'utente corrente
     */
    fun logout() {
        try {
            auth.signOut()
            _authState.value = AuthState.Unauthenticated
        } catch (e: Exception) {
            _authState.value = AuthState.Error("Errore durante il logout: ${e.message}")
        }
    }

    /**
     * Resetta lo stato di errore
     * Usato per pulire i messaggi di errore dopo che l'utente li ha visti
     */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}