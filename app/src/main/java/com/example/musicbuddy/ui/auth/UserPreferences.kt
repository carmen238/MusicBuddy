package com.example.musicbuddy.ui.auth

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

class UserPreferences(context: Context) {

    private val sharedPreferences =
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

        private const val KEY_INSTRUMENT = "instrument"
        private const val KEY_EXPERIENCE_LEVEL = "experienceLevel"
        private const val KEY_FAVORITE_GENRE = "favoriteGenre"

        private const val KEY_IS_IN_BAND = "isInBand"
    }

    // -------------------------
    // TOKEN
    // -------------------------

    fun saveAuthToken(token: String) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? =
        sharedPreferences.getString(KEY_AUTH_TOKEN, null)

    fun clearAuthToken() {
        sharedPreferences.edit().remove(KEY_AUTH_TOKEN).apply()
    }

    fun isUserLoggedIn(): Boolean =
        !getAuthToken().isNullOrEmpty()

    // -------------------------
    // ARRAY HELPERS
    // -------------------------

    private fun listToJson(list: List<String>?): String {
        return JSONArray(list ?: emptyList<String>()).toString()
    }

    private fun jsonToList(json: String?): List<String> {
        if (json.isNullOrEmpty()) return emptyList()
        val array = JSONArray(json)
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
        return list
    }

    // -------------------------
    // SAVE USER
    // -------------------------

    fun saveUserData(
        name: String,
        surname: String,
        email: String,
        phone: String,
        userId: Int,
        bio: String?,
        instrument: List<String>?,
        experienceLevel: String?,
        favoriteGenre: List<String>?,
        isInBand: Boolean?
    ) {
        sharedPreferences.edit().apply {
            putString(KEY_NAME, name)
            putString(KEY_SURNAME, surname)
            putString(KEY_EMAIL, email)
            putString(KEY_PHONE, phone)
            putString(KEY_BIO, bio ?: "")
            putInt(KEY_RATING, 0)
            putInt(KEY_USER_ID, userId)

            putString(KEY_INSTRUMENT, listToJson(instrument))
            putString(KEY_EXPERIENCE_LEVEL, experienceLevel ?: "")
            putString(KEY_FAVORITE_GENRE, listToJson(favoriteGenre))

            putBoolean(KEY_IS_IN_BAND, isInBand ?: false)

            apply()
        }
    }

    // -------------------------
    // GET USER
    // -------------------------

    fun getUserData(): Map<String, Any> {
        return mapOf(
            "userId" to sharedPreferences.getInt(KEY_USER_ID, 0),
            "name" to (sharedPreferences.getString(KEY_NAME, "") ?: ""),
            "surname" to (sharedPreferences.getString(KEY_SURNAME, "") ?: ""),
            "email" to (sharedPreferences.getString(KEY_EMAIL, "") ?: ""),
            "phone" to (sharedPreferences.getString(KEY_PHONE, "") ?: ""),
            "bio" to (sharedPreferences.getString(KEY_BIO, "") ?: ""),

            "instrument" to jsonToList(sharedPreferences.getString(KEY_INSTRUMENT, null)),
            "experienceLevel" to (sharedPreferences.getString(KEY_EXPERIENCE_LEVEL, "") ?: ""),
            "favoriteGenre" to jsonToList(sharedPreferences.getString(KEY_FAVORITE_GENRE, null)),

            "isInBand" to sharedPreferences.getBoolean(KEY_IS_IN_BAND, false)
        )
    }

    // -------------------------
    // SINGLE FIELD UPDATE
    // -------------------------

    fun saveUserField(field: String, value: Any) {
        sharedPreferences.edit().apply {

            when (field) {
                "instrument", "favoriteGenre" -> {
                    if (value is List<*>) {
                        putString(field, listToJson(value.filterIsInstance<String>()))
                    }
                }

                "isInBand" -> {
                    if (value is Boolean) {
                        putBoolean(KEY_IS_IN_BAND, value)
                    }
                }

                "userId" -> {
                    if (value is Int) putInt(KEY_USER_ID, value)
                }

                else -> {
                    putString(field, value.toString())
                }
            }

            apply()
        }
    }

    // -------------------------
    // CLEAR
    // -------------------------

    fun clearUserData() {
        sharedPreferences.edit().apply {
            remove(KEY_NAME)
            remove(KEY_SURNAME)
            remove(KEY_EMAIL)
            remove(KEY_PHONE)
            remove(KEY_BIO)
            remove(KEY_RATING)

            remove(KEY_INSTRUMENT)
            remove(KEY_EXPERIENCE_LEVEL)
            remove(KEY_FAVORITE_GENRE)
            remove(KEY_IS_IN_BAND)

            remove(KEY_USER_ID)
            apply()
        }
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}