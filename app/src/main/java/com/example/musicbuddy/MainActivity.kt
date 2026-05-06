package com.example.musicbuddy

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicbuddy.ui.auth.AuthViewModel
import com.example.musicbuddy.ui.navigation.NavigationGraph
import com.example.musicbuddy.ui.navigation.Screen
import com.example.musicbuddy.ui.theme.MusicBuddyTheme
import com.google.firebase.Firebase
import com.google.firebase.initialize
import kotlin.concurrent.thread

/**
 * Gestisce il layout generale e la navigazione
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // INIZIALIZZA FIREBASE IN UN THREAD SEPARATO
        thread {
            try {
                Firebase.initialize(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContent {
            MusicBuddyTheme {
                MusicBuddyApp()
            }
        }
    }
}

/**
 * Gestisce:
 * - NavController (pilota della navigazione)
 * - AuthViewModel (stato di autenticazione)
 * - Scaffold (struttura con navbar in basso)
 * - NavigationGraph (tutte le schermate)
 */
@Composable
fun MusicBuddyApp() {
    // Crea il NavController - gestisce la navigazione tra schermate
    val navController = rememberNavController()

    // Crea il ViewModel per l'autenticazione
    val authViewModel: AuthViewModel = viewModel()

    // Ottiene la schermata corrente dal back stack
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Definisce quali schermate devono mostrare la navbar
    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Profile.route,
        Screen.Tuner.route
    )

    Scaffold(
        // BottomNavigationBar - Navbar con 3 bottoni
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController, currentDestination)
            }
        }
    ) { paddingValues ->
        // NavigationGraph - Mostra le schermate
        NavigationGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            authViewModel = authViewModel
        )
    }
}

/**
 * BottomNavigationBar
 */
@Composable
fun BottomNavigationBar(
    navController: androidx.navigation.NavHostController,
    currentDestination: androidx.navigation.NavDestination?
) {
    // Definisce i bottoni della navbar
    val items = listOf(
        BottomNavItem(
            screen = Screen.Home,
            icon = Icons.Default.Home,
            label = "Home"
        ),
        BottomNavItem(
            screen = Screen.Search,
            icon = Icons.Default.Search,
            label = "Search"
        ),
        BottomNavItem(
            screen = Screen.Profile,
            icon = Icons.Default.Person,
            label = "Profile"
        )
    )

    NavigationBar {
        items.forEach { item ->

            val isSelected = currentDestination?.hierarchy?.any {
                it.route == item.screen.route
            } ?: false

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = {
                    
                    navController.navigate(item.screen.route) {
                        // Evita di creare più copie della stessa schermata
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)