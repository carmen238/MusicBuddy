package com.example.musicbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.musicbuddy.ui.auth.AuthState
import com.example.musicbuddy.ui.auth.AuthViewModel
import com.example.musicbuddy.ui.navigation.Screen
import com.example.musicbuddy.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignUpScreenMusicalProfile(
    authViewModel: AuthViewModel,
    name: String,
    surname: String,
    phone: String,
    email: String,
    password: String,
    onBackClick: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {},
    navController: NavController
) {

    // ===== STATES =====
    var selectedInstrument by remember { mutableStateOf("") }
    var selectedExperienceLevel by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("") }
    var isInBand by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Error -> {
                errorMessage = (authState as AuthState.Error).message
                showError = true

                scope.launch {
                    delay(3000L)
                    showError = false
                }
            }

            is AuthState.Authenticated -> {
                showError = false
                onSignUpSuccess()
            }

            else -> {
                showError = false
            }
        }
    }

    val isFormValid = selectedInstrument.isNotEmpty() &&
            selectedExperienceLevel.isNotEmpty() &&
            selectedGenre.isNotEmpty()

    val instruments = listOf(
        "🎸 Chitarra",
        "🎸 Basso",
        "🎺 Tromba",
        "🎻 Violino",
        "🪈 Flauto",
        "🎹 Pianoforte",
        "🥁 Batteria",
        "🎤 Voce",
        "🎵 Altro"
    )

    val experienceLevels = listOf(
        "🌱 Principiante",
        "📈 Intermedio",
        "⭐ Avanzato"
    )

    val genres = listOf(
        "🎸 Rock",
        "🎵 Pop",
        "🎷 Jazz",
        "🎼 Classico",
        "🎶 Blues",
        "🤘 Metal",
        "🌾 Folk",
        "🎵 Altro"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.LightBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // BACK
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }

            // IMAGE
            AsyncImage(
                model = "file:///android_asset/music_crowd_cut.jpg",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {

                Text("🎵 Musical Profile", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("Raccontaci di te come musicista")

                Spacer(modifier = Modifier.height(24.dp))

                // ===== STRUMENTO (UN SOLO) =====
                Text("🎸 Strumento Principale", fontWeight = FontWeight.Bold)
                Text("Seleziona uno strumento", fontSize = 12.sp, color = Color.Gray)

                instruments.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { instrument ->
                            Button(
                                onClick = { selectedInstrument = instrument },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor =
                                        if (selectedInstrument == instrument)
                                            AppColors.PrimaryGreen
                                        else AppColors.InputBackground
                                )
                            ) {
                                Text(instrument, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== EXPERIENCE =====
                Text("📊 Livello Esperienza", fontWeight = FontWeight.Bold)
                Text("Seleziona il tuo livello", fontSize = 12.sp, color = Color.Gray)

                experienceLevels.forEach { level ->
                    Button(
                        onClick = { selectedExperienceLevel = level },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (selectedExperienceLevel == level)
                                    AppColors.PrimaryGreen
                                else AppColors.InputBackground
                        )
                    ) {
                        Text(level)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== GENRE (UN SOLO) =====
                Text("🎵 Genere Preferito", fontWeight = FontWeight.Bold)
                Text("Seleziona un genere", fontSize = 12.sp, color = Color.Gray)

                genres.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { genre ->
                            Button(
                                onClick = { selectedGenre = genre },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor =
                                        if (selectedGenre == genre)
                                            AppColors.PrimaryGreen
                                        else AppColors.InputBackground
                                )
                            ) {
                                Text(genre, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== BAND TOGGLE =====
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("🎸 Sei in una band?", fontWeight = FontWeight.Bold)
                            Text(
                                if (isInBand) "Sì" else "No",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Switch(
                            checked = isInBand,
                            onCheckedChange = { isInBand = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ERROR
                if (showError) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Text(
                            text = errorMessage,
                            fontSize = 12.sp,
                            color = Color(0xFFC62828),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // LOADING
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp),
                        color = AppColors.PrimaryGreen
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== SIGNUP BUTTON =====
                Button(
                    onClick = {
                        authViewModel.signUp(
                            email = email,
                            password = password,
                            name = name,
                            surname = surname,
                            phone = phone,
                            instrument = selectedInstrument,
                            experienceLevel = selectedExperienceLevel,
                            genre = selectedGenre,
                            isInBand = isInBand,
                            onNavigateToStart = {
                                scope.launch {
                                    delay(3000L)
                                    navController.navigate(Screen.Start.route) {
                                        popUpTo(Screen.SignUp.route) { inclusive = true }
                                    }
                                }
                            }
                        )

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = isFormValid && authState !is AuthState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.PrimaryGreen,
                        disabledContainerColor = AppColors.DisabledButton
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Create Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}