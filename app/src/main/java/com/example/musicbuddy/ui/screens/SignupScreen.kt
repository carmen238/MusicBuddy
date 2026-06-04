package com.example.musicbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicbuddy.ui.auth.AuthState
import com.example.musicbuddy.ui.auth.AuthViewModel
import com.example.musicbuddy.ui.components.SignUpTextField
import com.example.musicbuddy.ui.components.Validators
import com.example.musicbuddy.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * SignUpScreen - Schermata di registrazione per MusicBuddy
 * Integrata con Firebase Authentication
 */
@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onContinueClick: (name: String, surname: String, phone: String, email: String, password: String) -> Unit,
    onBackClick: () -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Osserva lo stato di autenticazione
    val authState by authViewModel.authState.collectAsState()

    // Aggiorna il messaggio di errore quando lo stato cambia
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Error -> {
                errorMessage = (authState as AuthState.Error).message
                showError = true

                launch {
                    delay(3000L)
                    showError = false
                }
            }
            is AuthState.Authenticated -> {
                showError = false
            }
            else -> {
                showError = false
            }
        }
    }

    // Validazione globale
    val isFormValid =
        Validators.isValidName(name) &&
                Validators.isValidName(surname) &&
                Validators.isValidPhone(phone) &&
                Validators.isValidEmail(email) &&
                Validators.isValidPassword(password) &&
                Validators.verifyConfirmPassword(password, confirmPassword)

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
            // HEADER - Freccia indietro
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.DarkText,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            AsyncImage(
                model = "file:///android_asset/music_crowd_cut.jpg",
                contentDescription = "Crowd of people with music instruments",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
            )

            // CONTENUTO FORM
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // TITOLO
                Text(
                    text = "Sign Up",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.DarkText,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // CAMPO NAME
                SignUpTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Name",
                    placeholder = "Enter your name",
                    inputBackground = AppColors.InputBackground,
                    hintColor = AppColors.HintText,
                    textColor = AppColors.DarkText,
                    accentColor = AppColors.AccentYellow,
                    validator = { Validators.isValidName(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CAMPO SURNAME
                SignUpTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = "Surname",
                    placeholder = "Enter your surname",
                    inputBackground = AppColors.InputBackground,
                    hintColor = AppColors.HintText,
                    textColor = AppColors.DarkText,
                    accentColor = AppColors.AccentYellow,
                    validator = { Validators.isValidName(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CAMPO PHONE NUMBER
                SignUpTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone number",
                    placeholder = "Enter your phone number",
                    inputBackground = AppColors.InputBackground,
                    hintColor = AppColors.HintText,
                    textColor = AppColors.DarkText,
                    keyboardType = KeyboardType.Phone,
                    accentColor = AppColors.AccentYellow,
                    validator = { Validators.isValidPhone(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CAMPO EMAIL
                SignUpTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    placeholder = "Enter your email",
                    inputBackground = AppColors.InputBackground,
                    hintColor = AppColors.HintText,
                    textColor = AppColors.DarkText,
                    keyboardType = KeyboardType.Email,
                    accentColor = AppColors.AccentYellow,
                    validator = { Validators.isValidEmail(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CAMPO PASSWORD
                SignUpTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Enter your password",
                    inputBackground = AppColors.InputBackground,
                    hintColor = AppColors.HintText,
                    textColor = AppColors.DarkText,
                    isPassword = true,
                    accentColor = AppColors.AccentYellow,
                    validator = { Validators.isValidPassword(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CAMPO CONFIRM PASSWORD
                SignUpTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm password",
                    placeholder = "Confirm your password",
                    inputBackground = AppColors.InputBackground,
                    hintColor = AppColors.HintText,
                    textColor = AppColors.DarkText,
                    isPassword = true,
                    accentColor = AppColors.AccentYellow,
                    validator = { Validators.verifyConfirmPassword(password, confirmPassword) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // MESSAGGIO DI ERRORE
                if (showError) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
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

                // LOADING INDICATOR (NUOVO)
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = AppColors.PrimaryGreen
                    )
                }

                // BOTTONE CONTINUE
                Button(
                    onClick = {
                        onContinueClick(name, surname, phone, email, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.PrimaryGreen,
                        disabledContainerColor = AppColors.DisabledButton
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid && authState !is AuthState.Loading
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Preview - Anteprima della SignUpScreen
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpScreenPreview() {

}