package com.example.musicbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicbuddy.ui.screens.LoginScreen
import com.example.musicbuddy.ui.screens.SignUpScreen
import com.example.musicbuddy.ui.screens.StartScreen


/**
 * Definizione delle route dell'app
 * Ogni schermata ha un route univoco
 */
sealed class Screen(val route: String) {
    object Start : Screen("start_screen")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Search : Screen("search")
    object Profile : Screen("profile")
}

/**
 * NavigationGraph - Gestisce tutte le schermate e i loro collegamenti
 * Definisce quale schermata mostrare in base alla route
 */
@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Start.route,  // Schermata iniziale
        modifier = modifier
    ) {
        // SCHERMATA 1: StartScreen
        composable(Screen.Start.route) {
            StartScreen(
                onSignUpClick = {
                    // Naviga a SignUp quando clicchi il bottone "Sign Up"
                    navController.navigate(Screen.SignUp.route)
                },
                onLogInClick = {
                    // Naviga a Login quando clicchi il bottone "Log In"
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        // SCHERMATA 2: LoginScreen
        composable(Screen.Login.route) {
            LoginScreen(
                onContinueClick = { email, password ->
                    // Gestisci il login
                    println("Login: $email")
                    // Naviga a Home dopo il login
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Start.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    // Torna indietro quando clicchi la freccia
                    navController.popBackStack()
                }
            )
        }

        // SCHERMATA 3: SignUpScreen
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onContinueClick = { name, surname, phone, email, password ->
                    // Gestisci la registrazione
                    println("Registrazione: $name $surname, Email: $email")
                    // Naviga a Home dopo la registrazione
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Start.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    // Torna indietro quando clicchi la freccia
                    navController.popBackStack()
                }
            )
        }

//        // SCHERMATA 4: HomeScreen (schermata principale con navbar)
//        composable(Screen.Home.route) {
//            HomeScreen()
//        }
//
//        // SCHERMATA 5: SearchScreen (collegata alla navbar)
//        composable(Screen.Search.route) {
//            SearchScreen()
//        }
//
//        // SCHERMATA 6: ProfileScreen (collegata alla navbar)
//        composable(Screen.Profile.route) {
//            ProfileScreen()
//        }
    }
}