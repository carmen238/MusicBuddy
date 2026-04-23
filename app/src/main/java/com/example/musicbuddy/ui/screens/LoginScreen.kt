package com.example.musicbuddy.ui.screens

import android.R.attr.name
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
fun LoginScreen(
    onLogInClick: () -> Unit
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

        ) {
            // SEZIONE SUPERIORE - Logo/Branding
            MainSection(green)


            // SEZIONE INFERIORE - Bottoni di azione
            ButtonLogin(
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
fun MainSection(primaryColor: Color) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
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

        // HEADER - Titolo
        Text(
            text = "Sign Up",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 32.dp)
        )

        // CAMPO NAME
        SignUpTextField(
            value = name,
            onValueChange = { name = it },
            label = "Name",
            placeholder = "Enter your name",
            inputBackground = Color(0xFFFFFFFF),
            hintColor = Color(0xFF1F2937),
            textColor = Color(0xFF1F2937),
            accentColor = Color(0xFF1F2937)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * WelcomeSection - Testo di benvenuto e descrizione
 */


/**
 * ButtonSection - Bottoni di Sign Up e Log In
 */
@Composable
fun ButtonLogin(
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
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * SignUpTextField - Componente riutilizzabile per i campi di input
 */
@Composable
fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    inputBackground: Color,
    hintColor: Color,
    textColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        // Campo di input
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = hintColor,
                    fontSize = 14.sp
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = inputBackground,
                unfocusedContainerColor = inputBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Avatar con iniziale (solo per il primo campo)
        if (label == "Name" && value.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(50),
                color = accentColor
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = value.first().uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLogInClick = { }
    )
}