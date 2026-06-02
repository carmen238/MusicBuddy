package com.example.musicbuddy.ui.screens

import com.example.musicbuddy.ui.components.DropdownField
import ProfileInfoRow
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicbuddy.ui.auth.AuthViewModel
import com.example.musicbuddy.ui.auth.PhotoViewModel
import com.example.musicbuddy.ui.components.PhotoPickerButton
import com.example.musicbuddy.ui.theme.AppColors

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
    val userGenre = userData?.get("genre").toString() ?: ""
    val userIsInBand = (userData?.get("isInBand") ?: "false") as Boolean

    var bioEdit by remember { mutableStateOf(false) }
    var bioText by remember(userBio) { mutableStateOf(userBio) }

    val instruments = listOf("Chitarra", "Basso", "Piano", "Batteria", "Voce", "Altro")
    val experienceLevels = listOf("Principiante", "Intermedio", "Avanzato")
    val genres = listOf("Rock", "Pop", "Jazz", "Blues", "Metal", "Classico")
    val photoViewModel: PhotoViewModel = viewModel()
    val currentPhotoUrl = userData?.get("photo_url") as? String

    LaunchedEffect(Unit) {
        authViewModel.fetchUserData()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.LightBackground
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ================= HEADER =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Profilo",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.PrimaryGreen
                )
            }

            // ================= FOTO PROFILO CARD =================
            PhotoPickerButton(
                photoViewModel = photoViewModel,
                userId = userId,
                currentPhotoUrl = currentPhotoUrl,
                onPhotoSelected = { newPhotoUrl ->
                    authViewModel.updateUserField(userId, "photo_url", newPhotoUrl)
                }
            )
            Spacer(Modifier.height(12.dp))

            // ================= BIO SECTION =================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.LightBackground
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Bio",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = AppColors.PrimaryGreen
                        )
                        if (!bioEdit) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Modifica bio",
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { bioEdit = true },
                                tint = AppColors.PrimaryGreen
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (bioEdit) {
                        OutlinedTextField(
                            value = bioText,
                            onValueChange = { bioText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            placeholder = { Text("Scrivi la tua bio...") },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.PrimaryGreen,
                                unfocusedBorderColor = AppColors.InputBackground
                            )
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    bioEdit = false
                                    authViewModel.updateUserField(userId, "bio", bioText)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppColors.PrimaryGreen
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Salva", color = Color.White)
                            }

                            OutlinedButton(
                                onClick = {
                                    bioEdit = false
                                    bioText = userBio
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, AppColors.InputBackground)
                            ) {
                                Text("Annulla", color = AppColors.DarkText)
                            }
                        }
                    } else {
                        Text(
                            userBio.ifEmpty { "Nessuna bio" },
                            fontSize = 14.sp,
                            color = if (userBio.isEmpty()) AppColors.LightText else AppColors.DarkText
                        )
                    }
                }
            }

            // ================= INFORMAZIONI PERSONALI =================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.LightBackground
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Informazioni Personali",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = AppColors.PrimaryGreen,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    ProfileInfoRow("Nome", userName, "name", userId, authViewModel)
                    Divider(color = AppColors.InputBackground, thickness = 1.dp)
                    ProfileInfoRow("Cognome", userSurname, "surname", userId, authViewModel)
                    Divider(color = AppColors.InputBackground, thickness = 1.dp)
                    ProfileInfoRow("Email", userEmail, "email", userId, authViewModel)
                    Divider(color = AppColors.InputBackground, thickness = 1.dp)
                    ProfileInfoRow("Telefono", userPhone, "phone", userId, authViewModel)
                }
            }

            // ================= INFORMAZIONI MUSICALI =================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.LightBackground
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Profilo Musicale",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = AppColors.PrimaryGreen,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    DropdownField("Strumento", userInstrument, instruments) {
                        authViewModel.updateUserField(userId, "instrument", it)
                    }

                    Spacer(Modifier.height(12.dp))

                    DropdownField("Esperienza", userExperience, experienceLevels) {
                        authViewModel.updateUserField(userId, "experienceLevel", it)
                    }

                    Spacer(Modifier.height(12.dp))

                    DropdownField("Genere", userGenre, genres) {
                        authViewModel.updateUserField(userId, "genre", it)
                    }

                    Spacer(Modifier.height(16.dp))

                    // Band Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Sei in una band?",
                            fontSize = 14.sp,
                            color = AppColors.DarkText
                        )

                        Switch(
                            checked = userIsInBand,
                            onCheckedChange = {
                                authViewModel.updateUserField(
                                    userId,
                                    "isInBand",
                                    it.toString()
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AppColors.LightBackground,
                                checkedTrackColor = AppColors.PrimaryGreen
                            )
                        )
                    }
                }
            }

            // ================= LOGOUT BUTTON =================
            Button(
                onClick = {
                    authViewModel.logout()
                    onLogoutClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.AccentYellow
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp),
                    tint = AppColors.DarkText
                )
                Text(
                    "Logout",
                    color = AppColors.DarkText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}