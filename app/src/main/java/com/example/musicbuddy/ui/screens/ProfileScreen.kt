package com.example.musicbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicbuddy.ui.theme.AppColors

/**
 * ProfileScreen - Schermata del profilo utente
 * Mostra i dati dell'utente e permette il logout
 */
@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit = {}
) {
    // Dati hardcoded per ora
    val userName = "Jacopo"
    val userSurname = "Laurenza"
    val userEmail = "jacopo_laurenza@gmail.com"
    val userPhone = "922193156"
    val userBio = "I'm Jacopo and I'm 25-year-old active and athletic young man with a passion for sports and fitness. Whether it's running, playing soccer, or hitting the gym..."
    val userInitial = "J"
    val userRating = 3

    Surface(
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // HEADER - Titolo "Profile"
            Text(
                text = "Profilo",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )

            // CONTENUTO PRINCIPALE

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                                    text = userInitial,
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
                    Text(
                        text = userBio,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 24.dp),
                        lineHeight = 20.sp
                    )

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
                                    color = if (index < userRating) Color(0xFFCCCCCC) else Color(0xFFCCCCCC)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

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