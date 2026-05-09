package com.example.musicbuddy.ui.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * UserPreferences - Gestisce il salvataggio dei dati utente in SharedPreferences
 */
class UserPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    /**
     * Salva i dati dell'utente
     */
    fun saveUserData(name: String, surname: String, email: String, phone: String) {
        sharedPreferences.edit().apply {
            putString("name", name)
            putString("surname", surname)
            putString("email", email)
            putString("phone", phone)
            putString("bio", "")
            putInt("rating", 0)
            apply()
        }
    }

    /**
     * Recupera i dati dell'utente
     */
    fun getUserData(): Map<String, String> {
        return mapOf(
            "name" to (sharedPreferences.getString("name", "Nome") ?: "Nome"),
            "surname" to (sharedPreferences.getString("surname", "Cognome") ?: "Cognome"),
            "email" to (sharedPreferences.getString("email", "Email") ?: "Email"),
            "phone" to (sharedPreferences.getString("phone", "Telefono") ?: "Telefono"),
            "bio" to (sharedPreferences.getString("bio", "") ?: "")
        )
    }

    /**
     * Pulisce tutti i dati dell'utente
     */
    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }

    /**
     * Verifica se l'utente ha dati salvati
     */
    fun hasUserData(): Boolean {
        return sharedPreferences.contains("name")
    }
}