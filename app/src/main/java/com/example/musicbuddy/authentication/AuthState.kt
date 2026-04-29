package com.example.musicbuddy.ui.auth

import com.google.firebase.auth.FirebaseUser


/**
 * AuthState - Rappresenta i possibili stati di autenticazione dell'app
 * Usato per gestire il flusso di login/registrazione e la navigazione
 */
sealed class AuthState {
    /**
     * Idle - Stato iniziale, nessuna operazione in corso
     */
    object Idle : AuthState()

    /**
     * Loading - Operazione di autenticazione in corso (login/signup)
     */
    object Loading : AuthState()

    /**
     * Authenticated - Utente autenticato con successo
     * @param user Dati dell'utente Firebase
     */
    data class Authenticated(val user: FirebaseUser) : AuthState()

    /**
     * Unauthenticated - Utente non autenticato (logout o nessun login)
     */
    object Unauthenticated : AuthState()

    /**
     * Error - Errore durante l'autenticazione
     * @param message Messaggio di errore da mostrare all'utente
     */
    data class Error(val message: String) : AuthState()
}