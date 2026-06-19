package com.example.musicbuddy.ui.screens

import androidx.compose.runtime.Composable
import com.example.musicbuddy.ui.auth.AuthViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicbuddy.ui.components.SectionHeader
import com.example.musicbuddy.ui.components.FriendRow
import com.example.musicbuddy.data.models.*
import com.example.musicbuddy.ui.auth.FriendsViewModel
import com.example.musicbuddy.ui.theme.AppColors
import com.example.musicbuddy.ui.viewmodels.LocationViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FriendsScreen(
    authViewModel: AuthViewModel,
    onNavigateToChat: (friendId: Int) -> Unit,
    onBackClick: () -> Unit = {}
) {
    val userData by authViewModel.userData.collectAsState()

    val locationViewModel: LocationViewModel = viewModel()
    val friendsViewModel: FriendsViewModel = viewModel()

    val allFriends by friendsViewModel.allFriends.collectAsState()

    // User data
    val userId = userData?.get("userId") as? Int
    val userName = userData?.get("name") as? String ?: "Utente"
    val userGenre = userData?.get("genre").toString() ?: "Non specificato"
    val userInstrument = userData?.get("instrument") as? String ?: "Non specificato"
    val userExperience = userData?.get("experienceLevel") as? String ?: "Non specificato"
    val userIsInBand = userData?.get("isInBand") as? Boolean ?: false
    val currentPhotoUrl = userData?.get("photo_url") as? String

    var musiciansLoaded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Initialize
    LaunchedEffect(Unit) {
        authViewModel.fetchUserData()
        friendsViewModel.getAllFriends(userId as Int)
    }

    if(allFriends.isNotEmpty()) {
        musiciansLoaded = true
        println("AMICI: $allFriends")
    }

    //Funzioni da usare: acceptFriendRequest(...), deleteFriendRequest(...)

    // Funzioni stub per le tue chiamate Retrofit
    /*val cancelRequest: (Friend) -> Unit = { friend ->
        // TODO: Invocare API Retrofit per annullare la richiesta inviata
        uiState = uiState.copy(sentRequests = uiState.sentRequests.filter { it.id != friend.id })
    }

    val acceptRequest: (Friend) -> Unit = { friend ->
        // TODO: Invocare API Retrofit per accettare la richiesta
        uiState = uiState.copy(
            receivedRequests = uiState.receivedRequests.filter { it.id != friend.id },
            friendsList = uiState.friendsList + friend.copy(status = FriendStatus.ACCEPTED)
        )
    }

    val rejectRequest: (Friend) -> Unit = { friend ->
        // TODO: Invocare API Retrofit per rifiutare la richiesta ricevuta
        uiState = uiState.copy(receivedRequests = uiState.receivedRequests.filter { it.id != friend.id })
    }

    val removeFriend: (Friend) -> Unit = { friend ->
        // TODO: Invocare API Retrofit per rimuovere l'amico
        uiState = uiState.copy(friendsList = uiState.friendsList.filter { it.id != friend.id })
    }*/

    /*Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your friends list", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // SEZIONE 1: Richieste Ricevute (Pending in ingresso)
            if (/*uiState.receivedRequests.isNotEmpty()*/true) {
                stickyHeader { SectionHeader(title = "Received requests") }
                items(uiState.receivedRequests, key = { it.id }) { friend ->
                    FriendRow(
                        friend = friend,
                        actions = {
                            IconButton(onClick = { acceptRequest(friend) }) {
                                Icon(Icons.Default.Check, contentDescription = "Accetta", tint = Color.Green)
                            }
                            IconButton(onClick = { rejectRequest(friend) }) {
                                Icon(Icons.Default.Close, contentDescription = "Rifiuta", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                }
            }

            // SEZIONE 2: Richieste Inviate (Pending in uscita)
            if (uiState.sentRequests.isNotEmpty()) {
                stickyHeader { SectionHeader(title = "Sent requests (${uiState.sentRequests.size})") }
                items(uiState.sentRequests, key = { it.id }) { friend ->
                    FriendRow(
                        friend = friend,
                        actions = {
                            Button(
                                onClick = { cancelRequest(friend) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                            ) {
                                Text("Annulla", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    )
                }
            }

            // SEZIONE 3: Lista Amici (Accettati)
            stickyHeader { SectionHeader(title = "I Tuoi Amici (${uiState.friendsList.size})") }
            if (uiState.friendsList.isEmpty()) {
                item {
                    Text(
                        text = "Non hai ancora aggiunto amici. Inizia a suonare con qualcuno!",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(uiState.friendsList, key = { it.id }) { friend ->
                    FriendRow(
                        friend = friend,
                        actions = {
                            IconButton(onClick = { onNavigateToChat(friend.id) }) {
                                Icon(Icons.Default.Chat, contentDescription = "Apri Chat", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { removeFriend(friend) }) {
                                Icon(Icons.Default.PersonRemove, contentDescription = "Rimuovi Amico", tint = MaterialTheme.colorScheme.outline)
                            }
                        }
                    )
                }
            }
        }
    }*/

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.LightBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.LightBackground),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // HEADER - Freccia indietro
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.DarkText,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    "Friends list",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.DarkText
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.LightBackground)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //COLONNA AMICI ACCETTATI
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(2.dp, AppColors.PrimaryGreen)
                        .background(AppColors.LightBackground)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your Friends", fontSize = 18.sp)

                    // Linea Separatoria
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                    //...
                }

                //COLONNA RICHIESTE RICEVUTE
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(2.dp, AppColors.PrimaryGreen)
                        .background(AppColors.LightBackground)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Received friend requests", fontSize = 18.sp)

                    // Linea Separatoria
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                    //...
                }

                //COLONNA RICHIESTE INVIATE
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(2.dp, AppColors.PrimaryGreen)
                        .background(AppColors.LightBackground)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Pending friend requests", fontSize = 18.sp)

                    // Linea Separatoria
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                    //...
                }
            }
        }
    }
}