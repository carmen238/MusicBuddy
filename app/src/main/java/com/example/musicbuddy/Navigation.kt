package com.example.musicbuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
    object SignUpMusical : Screen("signup_musical/{name}/{surname}/{phone}/{email}/{password}")
    object Home : Screen("home")
    object Search : Screen("search")
    object Profile : Screen("profile")
    object Tuner : Screen("tuner")
    object Friends : Screen("friends")
    object Chat : Screen("chat/{friendId}/{friendName}/{friendSurname}")
}

/**
 * NavigationGraph - Gestisce tutte le schermate e i loro collegamenti
 * Integra Firebase Authentication per gestire il flusso di login/registrazione
 *
 * AGGIORNATO: Supporta SignUp in due schermate
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
        // ===== SCHERMATA 1: StartScreen =====
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

        // ===== SCHERMATA 2: SignUpScreen (Dati Personali) =====
        composable(Screen.SignUp.route) {
            val authState by authViewModel.authState.collectAsState()

            SignUpScreen(
                authViewModel = authViewModel,
                onContinueClick = { name, surname, phone, email, password ->
                    // Naviga alla schermata musicale passando i dati
                    navController.navigate(
                        "signup_musical/$name/$surname/$phone/$email/$password"
                    ) {
                        popUpTo(Screen.SignUp.route) { saveState = true }
                        launchSingleTop = true
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )

            // Se per qualche motivo la registrazione va a buon fine da questa schermata
            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Start.route) { inclusive = true }
                    }
                }
            }
        }

        // ===== SCHERMATA 3: SignUpScreenMusicalProfile (Profilo Musicale) =====
        composable(
            route = "signup_musical/{name}/{surname}/{phone}/{email}/{password}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("surname") { type = NavType.StringType },
                navArgument("phone") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val surname = backStackEntry.arguments?.getString("surname") ?: ""
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val password = backStackEntry.arguments?.getString("password") ?: ""

            SignUpScreenMusicalProfile(
                authViewModel = authViewModel,
                name = name,
                surname = surname,
                phone = phone,
                email = email,
                password = password,
                onBackClick = {
                    navController.popBackStack()
                },
                navController = navController,
                onSignUpSuccess = {
                    // Naviga a Home quando la registrazione è completata
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Start.route) { inclusive = true }
                    }
                }
            )
        }

        // ===== SCHERMATA 4: LoginScreen =====
        composable(Screen.Login.route) {
            val authState by authViewModel.authState.collectAsState()

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

        // ===== SCHERMATA 5: HomeScreen (schermata principale con navbar) =====
        composable(Screen.Home.route) {

            HomeScreen(
                authViewModel = authViewModel,
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                },
                onNavigateToTuner = {
                    navController.navigate(Screen.Tuner.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                },
                onNavigateToFriends = {
                    navController.navigate(Screen.Friends.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route) { popUpTo(Screen.Home.route) { inclusive = true } }
                }
            )
        }

        // SCHERMATA 8: TunerScreen
        composable(Screen.Tuner.route) {
            TunerScreen(tunerLogic = TunerLogic(), onBackClick = {
                navController.navigate(Screen.Home.route)
            })
        }

        // ===== SCHERMATA 6: SearchScreen (collegata alla navbar) =====
        composable(Screen.Search.route) {
            SearchScreen(authViewModel = authViewModel, onBackClick = {
                navController.navigate(Screen.Home.route)
            })
        }

        // ===== SCHERMATA 7: ProfileScreen (collegata alla navbar) =====
        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Start.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                onDeleteAccountClick = {
                    authViewModel.deleteAccount()
                    navController.navigate(Screen.Start.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                }
            )
        }

        // SCHERMATA 9: FriendsScreen
        composable(Screen.Friends.route) {
            FriendsScreen(authViewModel,
                onBackClick = {
                    navController.navigate(Screen.Home.route)
                },
                onNavigateToChat = {friendId, friendName, friendSurname ->
                    navController.navigate("chat/${friendId}/${friendName}/${friendSurname}") {
                        popUpTo(Screen.Friends.route) { saveState = true }
                        launchSingleTop = true
                    }
                })
        }

        composable(
            route = "chat/{friendId}",
            arguments = listOf(
                navArgument("friendId") { type = NavType.IntType },
                navArgument("friendName") { type = NavType.StringType },
                navArgument("friendSurname") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getInt("friendId") ?: 0
            val friendName = backStackEntry.arguments?.getString("friendName") ?: ""
            val friendSurname = backStackEntry.arguments?.getString("friendSurname") ?: ""

            ChatScreen(authViewModel, friendId, friendName, friendSurname, onBackClick = {
                navController.navigate(Screen.Friends.route)
            })
        }
    }
}