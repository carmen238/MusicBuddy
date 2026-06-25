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
import androidx.compose.ui.text.style.LineHeightStyle
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
import kotlinx.coroutines.delay

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
fun FriendAvatar(photoUrl: String?, name: String) {
    if (!photoUrl.isNullOrEmpty()) {
        AsyncImage(
            model = photoUrl,
            contentDescription = "Profile photo",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(AppColors.PrimaryGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.first().toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FriendInfoDialog(friend: FriendInfo, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                FriendAvatar(friend.photo_url, friend.name)

                Spacer(modifier = Modifier.height(12.dp))

                Column {
                    InfoRow("Name", friend.name)
                    InfoRow("Surname", friend.surname)
                    InfoRow("Bio", friend.bio)
                    InfoRow("Instrument", friend.instrument)
                    InfoRow("Experience", friend.experienceLevel)
                    InfoRow("Genre", friend.genre)
                    InfoRow("Band", if (friend.isInBand == 1) "Yes" else "No")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value ?: "-", fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
fun FriendRow(
    userId: Int,
    friend: FriendInfo,
    onNavigateToChat: (Int, String, String, Int) -> Unit,
    friendsViewModel: FriendsViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showInfo = true },
                verticalAlignment = Alignment.CenterVertically
            ) {

                FriendAvatar(friend.photo_url, friend.name)

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${friend.name} ${friend.surname}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Text(
                        friend.instrument ?: "",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                IconButton(onClick = {
                    onNavigateToChat(friend.id, friend.name, friend.surname, userId)
                }) {
                    Icon(Icons.Default.Chat, contentDescription = null)
                }

                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                InfoRow("Genre", friend.genre)
                InfoRow("Experience", friend.experienceLevel)
            }
        }
    }

    // DELETE DIALOG
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete friend?") },
            text = { Text("Do you really want to delete this friend?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    friendsViewModel.deleteFriendRequest(
                        friend.sender_id,
                        friend.receiver_id,
                        friend.sender_id == userId
                    )
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Back")
                }
            }
        )
    }

    if (showInfo) {
        FriendInfoDialog(friend) { showInfo = false }
    }
}

@Composable
fun ReceivedFriendRow(
    userId: Int,
    friend: FriendInfo,
    friendsViewModel: FriendsViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 🔹 Header (avatar + nome)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showInfo = true },
                verticalAlignment = Alignment.CenterVertically
            ) {

                FriendAvatar(friend.photo_url, friend.name)

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${friend.name} ${friend.surname}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Text(
                        friend.instrument ?: "",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Bottoni
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        friendsViewModel.acceptFriendRequest(userId, friend.id)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(AppColors.PrimaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ThumbUp, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Accept")
                }

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ThumbDown, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject")
                }
            }
        }
    }

    // 🔹 Dialog Reject
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Reject request?") },
            text = { Text("Do you really want to reject this friend request?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    friendsViewModel.deleteFriendRequest(
                        friend.sender_id,
                        friend.receiver_id,
                        friend.sender_id == userId
                    )
                }) {
                    Text("Reject", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Back")
                }
            }
        )
    }

    // 🔹 Dialog info
    if (showInfo) {
        FriendInfoDialog(friend) { showInfo = false }
    }
}

@Composable
fun SentFriendRow(
    userId: Int,
    friend: FriendInfo,
    friendsViewModel: FriendsViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 🔹 Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showInfo = true },
                verticalAlignment = Alignment.CenterVertically
            ) {

                FriendAvatar(friend.photo_url, friend.name)

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${friend.name} ${friend.surname}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Text(
                        friend.instrument ?: "",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Bottoni
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(Color.LightGray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sent")
                }

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cancel")
                }
            }
        }
    }

    // 🔹 Dialog cancel
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cancel request?") },
            text = { Text("Do you really want to cancel this friend request?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    friendsViewModel.deleteFriendRequest(
                        friend.sender_id,
                        friend.receiver_id,
                        friend.sender_id == userId
                    )
                }) {
                    Text("Cancel", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Back")
                }
            }
        )
    }

    // 🔹 Dialog info
    if (showInfo) {
        FriendInfoDialog(friend) { showInfo = false }
    }
}
