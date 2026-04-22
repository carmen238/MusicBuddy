package com.example.musicbuddy

import android.os.Bundle
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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicbuddy.ui.navigation.NavigationGraph
import com.example.musicbuddy.ui.navigation.Screen
import com.example.musicbuddy.ui.theme.MusicBuddyTheme

/**
 * MainActivity - Activity principale dell'app MusicBuddy
 * Gestisce il layout generale e la navigazione
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicBuddyTheme {
                MusicBuddyApp()
            }
        }
    }
}

/**
 * MusicBuddyApp - Composable principale
 * Gestisce:
 * - NavController (pilota della navigazione)
 * - Scaffold (struttura con navbar in basso)
 * - NavigationGraph (tutte le schermate)
 */
@Composable
fun MusicBuddyApp() {
    // Crea il NavController - gestisce la navigazione tra schermate
    val navController = rememberNavController()

    // Ottiene la schermata corrente dal back stack
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Definisce quali schermate devono mostrare la navbar
    // La navbar NON si mostra su: Start, Login, SignUp
    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Profile.route
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
            modifier = Modifier.padding(paddingValues)
        )
    }
}

/**
 * BottomNavigationBar - Navbar con 3 bottoni (Home, Search, Profile)
 * Mostra solo quando siamo nelle schermate principali
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
            // Verifica se il bottone è selezionato
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
                    // Naviga alla schermata quando clicchi il bottone
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

/**
 * BottomNavItem - Dati per ogni bottone della navbar
 */
data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)