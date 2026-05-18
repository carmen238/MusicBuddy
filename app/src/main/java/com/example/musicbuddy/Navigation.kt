package com.example.musicbuddy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicbuddy.ui.auth.AuthState
import com.example.musicbuddy.ui.auth.AuthViewModel
import com.example.musicbuddy.ui.components.TunerLogic
import com.example.musicbuddy.ui.screens.*

/**
 * Definizione delle route dell'app
 * Ogni schermata ha un route univoco
 */
sealed class Screen(val route: String) {
    object Start : Screen("start_screen")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object SignUp2 : Screen("signup2")
    object Home : Screen("home")
    object Search : Screen("search")
    object Profile : Screen("profile")
    object Tuner : Screen("tuner")
    object Chat : Screen("chat")
}

/**
 * NavigationGraph - Gestisce tutte le schermate e i loro collegamenti
 * Integra Firebase Authentication per gestire il flusso di login/registrazione
 */
@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
    // Osserva lo stato di autenticazione
    val authState by authViewModel.authState.collectAsState()

    // Determina la schermata iniziale in base allo stato di autenticazione
    val startDestination = when (authState) {
        is AuthState.Authenticated -> Screen.Home.route
        else -> Screen.Start.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // SCHERMATA 1: StartScreen
        composable(Screen.Start.route) {
            StartScreen(
                onSignUpClick = {
                    // Naviga a SignUp quando clicchi il bottone "Sign Up"
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.Start.route) { saveState = true }
                        launchSingleTop = true
                    }
                },
                onLogInClick = {
                    // Naviga a Login quando clicchi il bottone "Log In"
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Start.route) { saveState = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // SCHERMATA 2: LoginScreen
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onContinueClick = { email, password ->
                    authViewModel.login(email, password)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )

            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Start.route) { inclusive = true }
                    }
                }
            }
        }

        // SCHERMATA 3: SignUpScreen
        composable(Screen.SignUp.route) {
            SignUpScreen(
                authViewModel = authViewModel,
                onContinueClick = {
                    navController.navigate(Screen.SignUp2.route) {
                        popUpTo(Screen.Start.route) { saveState = true }
                        launchSingleTop = true
                    }
                    //authViewModel.signUp(email, password)
                },
                onBackClick = {
                    // Torna indietro quando clicchi la freccia
                    navController.popBackStack()
                }
            )
        }

        // SCHERMATA 3 (parte 2): SignUpScreen2
        composable(Screen.SignUp2.route) {
            SignUpScreen2(
                authViewModel = authViewModel,
                onCreateClick = { name, surname, phone, email, password, playedInstrument, favoriteMusicGenre, favoriteMusicSubgenre, currentFavoriteBand, profilePhoto ->
                    // Chiama la registrazione di Firebase tramite AuthViewModel
                    authViewModel.signUp(email, password)
                    //QUI FARE ROBA CON ALTRI PARAMETRI (MANDARLI AL NOSTRO DATABASE ATTRAVERSO L'API)
                    //...
                },
                onBackClick = {
                    // Torna indietro quando clicchi la freccia
                    navController.popBackStack()
                }
            )

            // Osserva lo stato di autenticazione e naviga se la registrazione ha successo
            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    // Registrazione riuscita, naviga a Home
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Start.route) { inclusive = true }
                    }
                }
            }
        }

        // SCHERMATA 4: HomeScreen (schermata principale con navbar)
        composable(Screen.Home.route) {
            HomeScreen(onNavigateToSearch = {
                navController.navigate(Screen.Search.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                },
                onNavigateToTuner = {
                    navController.navigate(Screen.Tuner.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                },
                onNavigateToChat = {
                    navController.navigate(Screen.Chat.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                })
        }

        // SCHERMATA 5: SearchScreen (collegata alla navbar)
        composable(Screen.Search.route) {
            SearchScreen()
        }

        // SCHERMATA 6: ProfileScreen (collegata alla navbar)
        composable(Screen.Profile.route) {
            ProfileScreen()
        }

        // SCHERMATA 7: TunerScreen
        composable(Screen.Tuner.route) {
            TunerScreen(tunerLogic = TunerLogic())
        }

        // SCHERMATA 8: ChatScreen
        composable(Screen.Chat.route) {
            ChatScreen()
        }
    }
}