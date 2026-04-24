package com.example.musicbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicbuddy.ui.components.SignUpTextField
import com.example.musicbuddy.ui.components.Validators
import com.example.musicbuddy.ui.theme.AppColors

/**
 * LoginScreen - Schermata di login per MusicBuddy
 */
@Composable
fun LoginScreen(
    onContinueClick: (email: String, password: String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Validazione
    val isFormValid =
        Validators.isValidEmail(email) &&
                Validators.isValidPassword(password)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFFFFF)
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
                    .fillMaxWidth()
                    .padding(16.dp),
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

            // IMMAGINE
            AsyncImage(
                model = "file:///android_asset/newyork_skyline.jpg",
                contentDescription = "Immagine skyline di New York",
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight(0.3f)
            )

            // CONTENUTO FORM
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // TITOLO
                Text(
                    text = "Login",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.DarkText,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(32.dp))

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

                Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(48.dp))

                // BOTTONE LOGIN
                Button(
                    onClick = {
                        onContinueClick(email, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.PrimaryGreen,
                        disabledContainerColor = AppColors.DisabledButton
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid
                ) {
                    Text(
                        text = "Log In",
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