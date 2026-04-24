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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicbuddy.ui.components.SignUpTextField
import com.example.musicbuddy.ui.components.Validators
import com.example.musicbuddy.ui.theme.AppColors

/**
 * SignUpScreen - Schermata di registrazione per MusicBuddy
 */
@Composable
fun SignUpScreen(
    onContinueClick: (name: String, surname: String, phone: String, email: String, password: String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

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
                model = "file:///android_asset/newyork_skyline.jpg",
                contentDescription = "Immagine skyline di New York",
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight(0.2f)
            )

            // CONTENUTO FORM
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // TITOLO
                Text(
                    text = "Sign Up",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.DarkText,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                    enabled = isFormValid
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
    SignUpScreen(
        onContinueClick = { _, _, _, _, _ -> },
        onBackClick = {}
    )
}