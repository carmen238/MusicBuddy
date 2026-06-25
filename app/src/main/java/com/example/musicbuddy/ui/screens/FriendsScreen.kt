package com.example.musicbuddy.ui.screens

import androidx.compose.runtime.Composable
import com.example.musicbuddy.ui.auth.AuthViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import com.example.musicbuddy.ui.components.ReceivedFriendRow
import com.example.musicbuddy.ui.components.SentFriendRow
import com.example.musicbuddy.ui.theme.AppColors
import com.example.musicbuddy.ui.viewmodels.LocationViewModel

//@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FriendsScreen(
    authViewModel: AuthViewModel,
    onNavigateToChat: (friendId: Int, friendName: String, friendSurname: String, userId: Int) -> Unit,
    onBackClick: () -> Unit = {}
) {
    val userData by authViewModel.userData.collectAsState()

    //val locationViewModel: LocationViewModel = viewModel()
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

    if (allFriends.isNotEmpty()) {
        musiciansLoaded = true
        println("AMICI: $allFriends")
    }

    //Funzioni da usare: acceptFriendRequest(...), deleteFriendRequest(...)

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
                verticalArrangement = Arrangement.Top,
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

                    if (!musiciansLoaded) {
                        Text(
                            "No friends found",
                            fontSize = 14.sp,
                            color = AppColors.LightText,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        var listTest = false
                        allFriends.forEach { friend ->
                            if ((friend.sender_id == userId || friend.receiver_id == userId) && friend.status == "ACCEPTED") {
                                listTest = true
                                FriendRow(
                                    userId = userId,
                                    friend = friend,
                                    onNavigateToChat = { id, name, surname, userId ->
                                        onNavigateToChat(id, name, surname, userId)
                                    },
                                    friendsViewModel = friendsViewModel
                                )
                            }
                        }
                        if (!listTest) {
                            Text(
                                "No friends found",
                                fontSize = 14.sp,
                                color = AppColors.LightText,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
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

                    if (!musiciansLoaded) {
                        Text(
                            "No requests found",
                            fontSize = 14.sp,
                            color = AppColors.LightText,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        var listTest = false
                        allFriends.forEach { friend ->
                            if (friend.sender_id == friend.id && friend.receiver_id == userId && friend.status == "PENDING") {
                                listTest = true
                                ReceivedFriendRow(userId, friend, friendsViewModel)
                            }
                        }
                        if (!listTest) {
                            Text(
                                "No requests found",
                                fontSize = 14.sp,
                                color = AppColors.LightText,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
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

                    if (!musiciansLoaded) {
                        Text(
                            "No requests found",
                            fontSize = 14.sp,
                            color = AppColors.LightText,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        var listTest = false
                        allFriends.forEach { friend ->
                            if (friend.sender_id == userId && friend.receiver_id == friend.id && friend.status == "PENDING") {
                                listTest = true
                                SentFriendRow(userId, friend, friendsViewModel)
                            }
                        }
                        if (!listTest) {
                            Text(
                                "No requests found",
                                fontSize = 14.sp,
                                color = AppColors.LightText,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}