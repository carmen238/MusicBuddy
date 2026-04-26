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

// Colori personalizzati per MusicBuddy (Blu e Viola)
val green = Color(0xFF337F00)      // Indigo/Blu
val yellow = Color(0xFFFDBC31)    // Viola
val lightBackground = Color(0xFFFFFFFF)  // Sfondo chiaro
val darkText = Color(0xFF1F2937)         // Testo scuro

@Composable
fun StartScreen(
    onSignUpClick: () -> Unit,
    onLogInClick: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = lightBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // SEZIONE SUPERIORE - Logo/Branding
            LogoSection(green)

            // SEZIONE CENTRALE - Testo di benvenuto
            WelcomeSection(green)

            // SEZIONE INFERIORE - Bottoni di azione
            ButtonSection(
                onSignUpClick = onSignUpClick,
                onLogInClick = onLogInClick,
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
fun LogoSection(primaryColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Placeholder per logo - Sostituisci con Image() o Icon()
        AsyncImage(
            model = "file:///android_asset/music_crowd_cut.jpg",
            contentDescription = "Crowd of people with music instruments",
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight(0.4f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nome app
        Text(
            text = "MusicBuddy",
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            color = green,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your music friends finder",
            fontSize = 16.sp,
            color = darkText,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * WelcomeSection - Testo di benvenuto e descrizione
 */
@Composable
fun WelcomeSection(textColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to MusicBuddy",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Find people to play music with near you. \nBe creative, together",
            fontSize = 16.sp,
            color = darkText,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

/**
 * ButtonSection - Bottoni di Sign Up e Log In
 */
@Composable
fun ButtonSection(
    onSignUpClick: () -> Unit,
    onLogInClick: () -> Unit,
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
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        // Bottone Log In - Outlined con colore secondario
        OutlinedButton(
            onClick = onLogInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = green,
            ),

            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Log In",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Preview - Anteprima della StartScreen in Android Studio
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StartScreenPreview() {
    StartScreen(
        onSignUpClick = { /* Preview action */ },
        onLogInClick = { /* Preview action */ }
    )
}