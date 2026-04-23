package com.example.musicbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * StartScreen - Landing page per MusicBuddy
 * Schermata iniziale con opzioni di Sign Up e Log In
 */
@Composable
fun SignupScreen(
    onSignUpClick: () -> Unit,
) {
    // Colori personalizzati per MusicBuddy (Blu e Viola)
    val green = Color(0xFF708F3B)      // Indigo/Blu
    val yellow = Color(0xFFFDBC31)    // Viola
    val lightBackground = Color(0xFFFFFFFF)  // Sfondo chiaro
    val darkText = Color(0xFF1F2937)         // Testo scuro

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = lightBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // SEZIONE SUPERIORE - Logo/Branding
            Section(green)

            // SEZIONE CENTRALE - Testo di benvenuto


            // SEZIONE INFERIORE - Bottoni di azione
            ButtonSignup(
                onSignUpClick = onSignUpClick,
                yellow = yellow,
                green = green
            )
        }
    }
}

/**
 * LogoSection - Area di branding con logo/icona musicale
 * Personalizza qui con la tua immagine o icona
 */
@Composable
fun Section(primaryColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Placeholder per logo - Sostituisci con Image() o Icon()
        AsyncImage(
            model = "file:///android_asset/newyork_skyline.jpg",
            contentDescription = "Immagine skyline di New York",
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight(0.4f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nome app
        Text(
            text = "MusicBuddy",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF708F3B),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Your Music Companion",
            fontSize = 14.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * WelcomeSection - Testo di benvenuto e descrizione
 */


/**
 * ButtonSection - Bottoni di Sign Up e Log In
 */
@Composable
fun ButtonSignup(
    onSignUpClick: () -> Unit,
    green: Color,
    yellow: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp)
            .padding(24.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bottone Sign Up - Filled con colore primario
        Button(
            onClick = onSignUpClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = yellow
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Sign Up",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

    }
}

/**
 * Preview - Anteprima della StartScreen in Android Studio
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignupScreenPreview() {
    SignupScreen(
        onSignUpClick = { /* Preview action */ },
    )
}