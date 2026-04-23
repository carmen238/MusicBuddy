package com.example.musicbuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * SignUpTextField - Componente riutilizzabile per i campi di input
 * Con validazione, feedback visivo e gestione password
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
    accentColor: Color,
    validator: ((String) -> Boolean)? = null
) {
    // Stato per visibilità password
    var showPassword by remember { mutableStateOf(false) }

    // Validazione
    val isValid = if (validator != null) validator(value) else value.isNotEmpty()
    val hasError = value.isNotEmpty() && !isValid

    Column(
        modifier = Modifier.fillMaxWidth()
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
                    focusedContainerColor = if (hasError) Color(0xFFFFEBEE) else inputBackground,
                    unfocusedContainerColor = if (hasError) Color(0xFFFFEBEE) else inputBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = when {
                    isPassword && !showPassword -> PasswordVisualTransformation()
                    else -> VisualTransformation.None
                },
                trailingIcon = {
                    when {
                        // Icona password visibility
                        isPassword -> {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (showPassword) "Hide password" else "Show password",
                                    tint = hintColor
                                )
                            }
                        }
                        // Icona di validazione (solo per campi non password)
                        value.isNotEmpty() && !isPassword -> {
                            Icon(
                                imageVector = if (isValid) Icons.Filled.Check else Icons.Filled.Close,
                                contentDescription = if (isValid) "Valid" else "Invalid",
                                tint = if (isValid) Color(0xFF4CAF50) else Color(0xFFE53935),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                },
                singleLine = true
            )

            // Avatar con iniziale (solo per il campo Name)
            if (label == "Name" && value.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterEnd)
                        .padding(end = 56.dp),
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

        // Messaggio di errore
        if (hasError) {
            Text(
                text = getErrorMessage(label),
                color = Color(0xFFE53935),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
            )
        }
    }
}

/**
 * Funzione helper per i messaggi di errore
 */
fun getErrorMessage(label: String): String {
    return when (label) {
        "Email" -> "Email non valida"
        "Phone number" -> "Numero di telefono non valido"
        "Password" -> "Password troppo corta (minimo 6 caratteri)"
        else -> "Campo non valido"
    }
}