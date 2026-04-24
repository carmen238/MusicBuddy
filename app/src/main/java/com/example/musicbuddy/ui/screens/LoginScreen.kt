package com.example.musicbuddy.ui.screens

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicbuddy.ui.components.SignUpTextField
import com.example.musicbuddy.ui.components.Validators
import com.example.musicbuddy.ui.theme.AppColors

@Composable
fun LoginScreen(
    onLogInClick: () -> Unit
) {
    val green = Color(0xFF708F3B)
    val yellow = Color(0xFFFDBC31)
    val lightBackground = Color(0xFFFFFFFF)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = lightBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),

        ) {
            MainSection()
            // Login button
            ButtonLogin(
                onLogInClick = onLogInClick,
                green = green
            )
        }
    }
}

@Composable
fun MainSection() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                .fillMaxHeight(0.4f)
        )

       Column(
           modifier = Modifier.
           padding(horizontal = 23.dp)
       ) {
           Spacer(modifier = Modifier.height(16.dp))

           // HEADER - Titolo
           Text(
               text = "Login",
               fontSize = 32.sp,
               fontWeight = FontWeight.Bold,
               color = Color(0xFF1F2937),
               modifier = Modifier
                   .align(Alignment.Start)

           )


           Spacer(modifier = Modifier.height(16.dp))

           // CAMPO PASSWORD
           SignUpTextField(
               value = email,
               onValueChange = { password = it },
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
               isPassword = true,
               accentColor = AppColors.AccentYellow,
               validator = { Validators.isValidPassword(it) }
           )

           Spacer(modifier = Modifier.height(26.dp))
       }
    }
}


@Composable
fun ButtonLogin(
    onLogInClick: () -> Unit,
    green: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        OutlinedButton(
            onClick = onLogInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 23.dp)
            ,
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

@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLogInClick = { }
    )
}