package com.example.musicbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicbuddy.ui.components.SignUpTextField
import com.example.musicbuddy.ui.components.Validators
import com.example.musicbuddy.ui.theme.AppColors

@Composable
fun SignupScreen(
    onSignUpClick: () -> Unit,
) {

    val green = Color(0xFF708F3B)
    val yellow = Color(0xFFFDBC31)
    val lightBackground = Color(0xFFFFFFFF)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = lightBackground,
    ) {
        Column(

            verticalArrangement = Arrangement.SpaceBetween
        ) {

            MainSectionSignup()

            // BOTTOLE SIGNUP
            ButtonSignup(
                onSignUpClick = onSignUpClick,
                yellow = yellow,
                green = green
            )
        }
    }
}


@Composable
fun MainSectionSignup() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "file:///android_asset/newyork_skyline.jpg",
            contentDescription = "Immagine skyline di New York",
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight(0.3f)
        )

        Column(
            modifier = Modifier.
            padding(horizontal = 23.dp)
        ) {
            // HEADER - Titolo
            Text(
                text = "Signup",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier
                    .align(Alignment.Start)

            )
            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO name
            SignUpTextField(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                placeholder = "Enter your name",
                inputBackground = AppColors.InputBackground,
                hintColor = AppColors.HintText,
                textColor = AppColors.DarkText,
                accentColor = AppColors.AccentYellow,
                validator = { Validators.isValidEmail(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO surname
            SignUpTextField(
                value = surname,
                onValueChange = { surname = it },
                label = "Surname",
                placeholder = "Enter your surname",
                inputBackground = AppColors.InputBackground,
                hintColor = AppColors.HintText,
                textColor = AppColors.DarkText,
                accentColor = AppColors.AccentYellow,
                validator = { Validators.isValidEmail(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO Email
            SignUpTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "Enter your email",
                inputBackground = AppColors.InputBackground,
                hintColor = AppColors.HintText,
                textColor = AppColors.DarkText,
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
                accentColor = AppColors.AccentYellow,
                isPassword = true,
                validator = { Validators.isValidPassword(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO  CONFIRM PASSWORD
            SignUpTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm password",
                placeholder = "Enter your confirm password",
                inputBackground = AppColors.InputBackground,
                hintColor = AppColors.HintText,
                textColor = AppColors.DarkText,
                isPassword = true,
                accentColor = AppColors.AccentYellow,
                validator = { Validators.verifyConfirmPassword(password,confirmPassword) }
            )

        }
    }
}

/**
 * ButtonSection
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
            .padding(23.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignupScreenPreview() {
    SignupScreen(
        onSignUpClick = { /* Preview action */ },
    )
}