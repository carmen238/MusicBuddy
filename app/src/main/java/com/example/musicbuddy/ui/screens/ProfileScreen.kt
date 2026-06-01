package com.example.musicbuddy.ui.screens

import DropdownField
import ProfileInfoRow
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicbuddy.ui.auth.AuthViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogoutClick: () -> Unit = {}
) {

    val userData by authViewModel.userData.collectAsState()

    val userId = userData?.get("userId").toString()
    val userName = userData?.get("name") as? String ?: ""
    val userSurname = userData?.get("surname") as? String ?: ""
    val userEmail = userData?.get("email") as? String ?: ""
    val userPhone = userData?.get("phone") as? String ?: ""
    val userBio = userData?.get("bio") as? String ?: ""

    val userInstrument = userData?.get("instrument") as? String ?: ""
    val userExperience = userData?.get("experienceLevel") as? String ?: ""
    val userGenre = userData?.get("genre").toString()?: ""
    val userIsInBand = (userData?.get("isInBand") ?: "false") as Boolean

    var bioEdit by remember { mutableStateOf(false) }
    var bioText by remember(userBio) { mutableStateOf(userBio) }

    val instruments = listOf("Chitarra", "Basso", "Piano", "Batteria", "Voce", "Altro")
    val experienceLevels = listOf("Principiante", "Intermedio", "Avanzato")
    val genres = listOf("Rock", "Pop", "Jazz", "Blues", "Metal", "Classico")

    LaunchedEffect(Unit) {
        authViewModel.fetchUserData()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {

        Column(
            Modifier
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Text("Profile", fontSize = 32.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(24.dp))

            // ================= FOTO PROFILO =================
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.toString() ?: "U",
                    fontSize = 40.sp,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(24.dp))

            // ================= BIO (RESTORED) =================
            Text("Bio", fontWeight = FontWeight.Bold)

            if (bioEdit) {
                println("userId:" + userData + userInstrument)
                OutlinedTextField(
                    value = bioText,
                    onValueChange = { bioText = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Row {
                    Button(onClick = {
                        bioEdit = false
                        authViewModel.updateUserField(
                            userId,
                            "bio",
                            bioText
                        )
                    }) {
                        Text("Save")
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(onClick = {
                        bioEdit = false
                        bioText = userBio
                    }) {
                        Text("Cancel")
                    }
                }

            } else {
                Text(userBio.ifEmpty { "No bio" })

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.clickable { bioEdit = true }
                )
            }

            Spacer(Modifier.height(24.dp))

            // ================= BASIC INFO =================
            ProfileInfoRow("Nome", userName, "name", userId, authViewModel)
            ProfileInfoRow("Cognome", userSurname, "surname", userId, authViewModel)
            ProfileInfoRow("Email", userEmail, "email", userId, authViewModel)
            ProfileInfoRow("Telefono", userPhone, "phone", userId, authViewModel)

            Spacer(Modifier.height(24.dp))

            // ================= MUSIC INFO (DROPDOWN STYLE) =================

            DropdownField("Strumento", userInstrument, instruments) {
                authViewModel.updateUserField(userId, "instrument", it)
            }

            DropdownField("Esperienza", userExperience, experienceLevels) {
                authViewModel.updateUserField(userId, "experienceLevel", it)
            }

            DropdownField("Genere", userGenre, genres) {
                authViewModel.updateUserField(userId, "genre", it)
            }

            Spacer(Modifier.height(16.dp))

            // ================= BAND SWITCH =================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sei in una band?", fontSize = 14.sp)

                Switch(
                    checked = userIsInBand,
                    onCheckedChange = {
                        authViewModel.updateUserField(
                            userId,
                            "isInBand",
                            it.toString()
                        )
                    }
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    onLogoutClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935) // rosso logout
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Logout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

