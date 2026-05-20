package com.example.musicbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicbuddy.ui.auth.AuthViewModel
import com.example.musicbuddy.ui.theme.AppColors

/**
 * ProfileScreen - Schermata del profilo utente
 * Mostra i dati dell'utente recuperati da Firebase
 */
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogoutClick: () -> Unit = {}
) {
    // Recupera i dati dell'utente
    val userData by authViewModel.userData.collectAsState()

    // Estrai i dati
    val userId = userData?.get("userId") as? String
    val userName = userData?.get("name") as? String ?: "Nome"
    val userSurname = userData?.get("surname") as? String ?: "Cognome"
    val userEmail = userData?.get("email") as? String ?: "Email"
    val userPhone = userData?.get("phone") as? String ?: "Telefono"
    val userBio = userData?.get("bio") as? String ?: "Bio non disponibile"
    val userInitial = userName.firstOrNull()?.uppercaseChar() ?: "U"
    val userRating = (userData?.get("rating") as? Number)?.toInt() ?: 0

    // Carica i dati quando la schermata si apre
    LaunchedEffect(Unit) {
        authViewModel.fetchUserData()
    }

    @Composable
    fun BioInput() {
        var bio by remember { mutableStateOf("") }

        Column {
            Text(text = "Inserisci bio")

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                placeholder = { Text(if (userBio.isNotEmpty()) userBio else "Scrivi qui...") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )

            // BOTTONE LOG OUT
            Button(
                onClick = {

                    authViewModel.updateUserField(
                        userId?.toInt(),
                        "bio",
                        userBio
                    )
                },
                modifier = Modifier
                    .width(36.dp)
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.PrimaryGreen
                ),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = "+",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        // CONTENUTO PRINCIPALE
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TITOLO "Profile"
                Text(
                    text = "Profile",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 24.dp)
                )

                // SEZIONE FOTO PROFILO
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // AVATAR CON INIZIALE
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8FA3B8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userInitial.toString(),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // ICONA FOTOCAMERA
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit photo",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(top = 8.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }

                // NOME UTENTE
                Text(
                    text = userName,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 16.dp)
                )

                // BIO

                BioInput()



                if (userBio.isNotEmpty()) {
                    Text(
                        text = userBio,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 24.dp),
                        lineHeight = 20.sp
                    )
                }

                // SEZIONE "Personal Info"
                Text(
                    text = "Personal Info",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 16.dp)
                )

                // CAMPO NOME
                ProfileInfoRow(
                    label = "Nome",
                    value = userName
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO COGNOME
                ProfileInfoRow(
                    label = "Cognome",
                    value = userSurname
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO EMAIL
                ProfileInfoRow(
                    label = "Email",
                    value = userEmail
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO TELEFONO
                ProfileInfoRow(
                    label = "Mobile Phone",
                    value = userPhone
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO RATING
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rating",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    // STELLE
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(5) { index ->
                            Text(
                                text = "★",
                                fontSize = 20.sp,
                                color = if (index < userRating) Color(0xFFCCCCCC) else Color(
                                    0xFFCCCCCC
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(52.dp))

                // BOTTONE LOG OUT
                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.PrimaryGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Log Out",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}


/**
 * ProfileInfoRow - Componente per mostrare una riga di informazioni
 */
@Composable
fun ProfileInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = AppColors.PrimaryGreen,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}