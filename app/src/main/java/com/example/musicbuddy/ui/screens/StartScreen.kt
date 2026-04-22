package com.example.musicbuddy.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage


/**
 * StartScreen - Landing page per MusicBuddy
 * Schermata iniziale con opzioni di Sign Up e Log In
 */
@Composable
fun StartScreen(
    onSignUpClick: () -> Unit,
    onLogInClick: () -> Unit
) {
    // Colori personalizzati per MusicBuddy (Blu e Viola)
    val primaryBlue = Color(0xFF6366F1)      // Indigo/Blu
    val accentPurple = Color(0xFF8B5CF6)     // Viola
    val lightBackground = Color(0xFFF8F9FA)  // Sfondo chiaro
    val darkText = Color(0xFF1F2937)         // Testo scuro

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = lightBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // SEZIONE SUPERIORE - Logo/Branding
            LogoSection(primaryBlue)

            // SEZIONE CENTRALE - Testo di benvenuto
            WelcomeSection(darkText)

            // SEZIONE INFERIORE - Bottoni di azione
            ButtonSection(
                onSignUpClick = onSignUpClick,
                onLogInClick = onLogInClick,
                primaryBlue = primaryBlue,
                accentPurple = accentPurple
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
    AsyncImage(
        model = "https://it.wikipedia.org/wiki/JPEG#/media/File:Felis_silvestris_silvestris_small_gradual_decrease_of_quality.png",
        contentDescription = "Logo caricato dal web",
        modifier = Modifier
            .fillMaxWidth(0.5f) // Prende il 50% della larghezza disponibile
            .height(200.dp),    // Altezza fissa
        contentScale = ContentScale.Fit
    )
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
            text = "Discover, stream, and enjoy your favorite music anytime, anywhere",
            fontSize = 14.sp,
            color = Color(0xFF6B7280),
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
    primaryBlue: Color,
    accentPurple: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bottone Sign Up - Filled con colore primario
        Button(
            onClick = onSignUpClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = accentPurple
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

        // Bottone Log In - Outlined con colore secondario
        OutlinedButton(
            onClick = onLogInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = primaryBlue
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Log In",
                fontSize = 16.sp,
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