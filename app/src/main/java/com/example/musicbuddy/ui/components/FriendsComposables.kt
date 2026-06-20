package com.example.musicbuddy.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.musicbuddy.data.models.*
import com.example.musicbuddy.ui.auth.AuthViewModel
import com.example.musicbuddy.ui.auth.FriendsViewModel
import com.example.musicbuddy.ui.components.*
import com.example.musicbuddy.ui.theme.AppColors

@Composable
fun SectionHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun FriendRow(
    userId: Int,
    friend: FriendInfo,
    onNavigateToChat: (friendId: Int, friendName: String, friendSurname: String) -> Unit,
    friendsViewModel: FriendsViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var showFriendInfo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clickable { showFriendInfo = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (friend.photo_url != "") {
                    AsyncImage(
                        model = friend.photo_url,
                        contentDescription = "Profile photo",
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Crop
                    )

                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Dettagli del musicista
                Column {
                    Text(
                        friend.name + " " + friend.surname,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.DarkText
                    )
                }
            }

            // Slot per i bottoni contestuali passati dal parent
            Row(verticalAlignment = Alignment.CenterVertically) {
                //Chat button
                Button(
                    onClick = { onNavigateToChat(friend.id, friend.name, friend.surname) },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.PrimaryGreen
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )

                    Text("Chat", color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                }
                //Delete friend button
                Button(
                    onClick = {showDialog = true},
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )

                    Text("Delete", color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    // Si attiva quando l'utente tocca fuori dal popup o preme "Indietro"
                    showDialog = false
                },
                title = {Text(text = "Delete friend?") },
                text = {Text(text = "Do you really want to delete this friend?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            friendsViewModel.deleteFriendRequest(friend.sender_id, friend.receiver_id)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,  // Background color
                            contentColor = Color.White    // Text color
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false }, // Chiude il popup
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,  // Background color
                            contentColor = Color.White    // Text color
                        )
                    ) {
                        Text("Back")
                    }
                }
            )
        }

        if (showFriendInfo) {
            Dialog(
                onDismissRequest = { showFriendInfo = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (friend.photo_url != "") {
                            AsyncImage(
                                model = friend.photo_url,
                                contentDescription = "Profile photo",
                                modifier = Modifier.size(40.dp),
                                contentScale = ContentScale.Crop
                            )

                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dettagli del musicista
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Name: "+friend.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Surname: "+friend.surname,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Biography: "+friend.bio,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Instrument: "+friend.instrument,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Experience: "+friend.experienceLevel,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Genre: "+friend.genre,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Band: "+ if(friend.isInBand==1) "Yes" else "No",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(onClick = { showFriendInfo = false },
                            colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,  // Background color
                            contentColor = Color.White    // Text color
                        )) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReceivedFriendRow(
    userId: Int,
    friend: FriendInfo,
    friendsViewModel: FriendsViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var showFriendInfo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clickable { showFriendInfo = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (friend.photo_url != "") {
                    AsyncImage(
                        model = friend.photo_url,
                        contentDescription = "Profile photo",
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Crop
                    )

                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Dettagli del musicista
                Column {
                    Text(
                        friend.name + " " + friend.surname,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.DarkText
                    )
                }
            }

            // Slot per i bottoni contestuali passati dal parent
            Row(verticalAlignment = Alignment.CenterVertically) {
                //Accept button
                Button(
                    onClick = {
                        friendsViewModel.acceptFriendRequest(userId, friend.id)
                        friendsViewModel.getAllFriends(userId) },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.PrimaryGreen
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )

                    Text("Accept", color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                }
                //Reject friend button
                Button(
                    onClick = {showDialog = true},
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )

                    Text("Reject", color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    // Si attiva quando l'utente tocca fuori dal popup o preme "Indietro"
                    showDialog = false
                },
                title = {Text(text = "Delete this friend request?") },
                text = {Text(text = "Do you really want to reject this friend request?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            friendsViewModel.deleteFriendRequest(friend.sender_id, friend.receiver_id)
                            friendsViewModel.getAllFriends(userId)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,  // Background color
                            contentColor = Color.White    // Text color
                        )
                    ) {
                        Text("Reject")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false }, // Chiude il popup
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,  // Background color
                            contentColor = Color.White    // Text color
                        )
                    ) {
                        Text("Back")
                    }
                }
            )
        }

        if (showFriendInfo) {
            Dialog(
                onDismissRequest = { showFriendInfo = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (friend.photo_url != "") {
                            AsyncImage(
                                model = friend.photo_url,
                                contentDescription = "Profile photo",
                                modifier = Modifier.size(40.dp),
                                contentScale = ContentScale.Crop
                            )

                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dettagli del musicista
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Name: "+friend.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Surname: "+friend.surname,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Biography: "+friend.bio,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Instrument: "+friend.instrument,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Experience: "+friend.experienceLevel,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Genre: "+friend.genre,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Band: "+ if(friend.isInBand==1) "Yes" else "No",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(onClick = { showFriendInfo = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray,  // Background color
                                contentColor = Color.White    // Text color
                            )) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SentFriendRow(
    userId: Int,
    friend: FriendInfo,
    friendsViewModel: FriendsViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var showFriendInfo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .clickable { showFriendInfo = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (friend.photo_url != "") {
                    AsyncImage(
                        model = friend.photo_url,
                        contentDescription = "Profile photo",
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Crop
                    )

                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Dettagli del musicista
                Column {
                    Text(
                        friend.name + " " + friend.surname,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.DarkText
                    )
                }
            }

            // Slot per i bottoni contestuali passati dal parent
            Row(verticalAlignment = Alignment.CenterVertically) {
                //Sent button
                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )

                    Text("Sent", color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                }
                //Cancel friend button
                Button(
                    onClick = {showDialog = true},
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )

                    Text("Cancel", color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    // Si attiva quando l'utente tocca fuori dal popup o preme "Indietro"
                    showDialog = false
                },
                title = {Text(text = "Delete this friend request?") },
                text = {Text(text = "Do you really want to cancel this friend request?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            friendsViewModel.deleteFriendRequest(friend.sender_id, friend.receiver_id)
                            friendsViewModel.getAllFriends(userId)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,  // Background color
                            contentColor = Color.White    // Text color
                        )
                    ) {
                        Text("Cancel")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false }, // Chiude il popup
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,  // Background color
                            contentColor = Color.White    // Text color
                        )
                    ) {
                        Text("Back")
                    }
                }
            )
        }

        if (showFriendInfo) {
            Dialog(
                onDismissRequest = { showFriendInfo = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (friend.photo_url != "") {
                            AsyncImage(
                                model = friend.photo_url,
                                contentDescription = "Profile photo",
                                modifier = Modifier.size(40.dp),
                                contentScale = ContentScale.Crop
                            )

                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dettagli del musicista
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Name: "+friend.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Surname: "+friend.surname,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Biography: "+friend.bio,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Instrument: "+friend.instrument,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Experience: "+friend.experienceLevel,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Genre: "+friend.genre,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                            Text(
                                "Band: "+ if(friend.isInBand==1) "Yes" else "No",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.DarkText
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(onClick = { showFriendInfo = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray,  // Background color
                                contentColor = Color.White    // Text color
                            )) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}
