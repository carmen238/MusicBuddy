package com.example.musicbuddy.ui.navigation

/**
 * Screen - Enum che definisce tutte le schermate dell'app
 * Ogni schermata ha una rotta unica
 */
sealed class Screens(val route: String) {
    // Schermate di autenticazione (senza navbar)
    object Start : Screen("start_screen")
    object SignUp : Screen("signup_screen")
    object Login : Screen("login_screen")

    // Schermate principali (con navbar)
    object Home : Screen("home_screen")
    object Search : Screen("search_screen")
    object Profile : Screen("profile_screen")
}