package com.example.musicbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicbuddy.ui.auth.AuthViewModel
import com.example.musicbuddy.ui.theme.AppColors

/**
 * Funzione helper per la validazione dei campi
 */
fun validateField(field: String, value: String): Boolean {
    return when (field) {
        "email" -> {
            val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
            emailRegex.matches(value)
        }
        "phone" -> {
            val phoneRegex = Regex("^[0-9+\\-\\s()]{7,}$")
            phoneRegex.matches(value)
        }
        "name", "surname" -> value.length >= 2
        else -> value.isNotEmpty()
    }
}

/**
 * Funzione helper per i messaggi di errore
 */
fun getFieldErrorMessage(field: String): String {
    return when (field) {
        "email" -> "Email non valida"
        "phone" -> "Numero di telefono non valido"
        "name" -> "Nome deve avere almeno 2 caratteri"
        "surname" -> "Cognome deve avere almeno 2 caratteri"
        else -> "Campo non valido"
    }
}

/**
 * ProfileScreen - Schermata del profilo utente
 * Mostra i dati dell'utente recuperati da Firebase
 */
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogoutClick: () -> Unit = {}
) {
    // Recupera i dati dell'utente
    val userData by authViewModel.userData.collectAsState()

    // Estrai i dati
    val userId = userData?.get("userId") as? String
    val userName = userData?.get("name") as? String ?: "Nome"
    val userSurname = userData?.get("surname") as? String ?: "Cognome"
    val userEmail = userData?.get("email") as? String ?: "Email"
    val userPhone = userData?.get("phone") as? String ?: "Telefono"
    val userBio = userData?.get("bio") as? String ?: "Bio non disponibile"
    val userInitial = userName.firstOrNull()?.uppercaseChar() ?: "U"

    var notEditingField by remember { mutableStateOf(true) }


    // Carica i dati quando la schermata si apre
    LaunchedEffect(Unit) {
        authViewModel.fetchUserData()
        println("user" + userData)
    }

    @Composable
    fun ProfileInfoRow(
        label: String,
        value: String,
        field: String
    ) {
        // Stato per capire se siamo in modalità modifica
        var isEditing by remember { mutableStateOf(false) }
        var textValue by remember(value) { mutableStateOf(value) }

        // Validazione
        val isValid = validateField(field, textValue)
        val hasError = textValue.isNotEmpty() && !isValid

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditing) {
                        Row {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Conferma",
                                tint = if (isValid) AppColors.PrimaryGreen else Color.Gray,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable(enabled = isValid) {
                                        isEditing = false
                                        authViewModel.updateUserField(
                                            userId?.toInt(),
                                            field,
                                            textValue
                                        )
                                    }
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Annulla modifiche",
                                tint = AppColors.ErrorRed,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable {
                                        isEditing = false
                                        textValue = value
                                    }
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifica",
                            tint = AppColors.PrimaryGreen,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    isEditing = true // Attiva la modifica
                                }
                        )
                    }

                    if (isEditing) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(
                                    color = if (hasError) Color(0xFFFFEBEE) else Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = textValue,
                                onValueChange = { textValue = it },
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Text(
                            text = value,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                }
            }

            // Messaggio di errore
            if (hasError) {
                Text(
                    text = getFieldErrorMessage(field),
                    color = Color(0xFFE53935),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }
        }
    }


    @Composable
    fun BioInput(
        onDiscard: () -> Unit
    ) {
        var bio by remember { mutableStateOf("") }

        val focusRequester = remember { FocusRequester() }
        var hasBeenFocused by remember { mutableStateOf(false) }

        // Richiede il focus AUTOMATICAMENTE appena compare il componente
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                placeholder = { Text("Scrivi qui...") },
                maxLines = 5,
                modifier = Modifier
                    .weight(0.85f)
                    .focusRequester(focusRequester) // Collega il gestore del focus
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused || focusState.hasFocus) {
                            // Il campo ha preso il focus con successo
                            hasBeenFocused = true
                        } else if (hasBeenFocused) {
                            // Chiude SOLO se aveva il focus in precedenza e ora lo ha perso
                            onDiscard()
                        }
                    }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    notEditingField = !notEditingField
                    authViewModel.updateUserField(
                        userId?.toInt(),
                        "bio",
                        bio
                    )
                },
                enabled = bio.isNotBlank(),
                modifier = Modifier.size(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.PrimaryGreen,
                    disabledContainerColor = AppColors.PrimaryGreen.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(50.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "+",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }



    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        // CONTENUTO PRINCIPALE
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = CenterHorizontally
            ) {
                // TITOLO "Profile"
                Text(
                    text = "Profile",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 24.dp)
                )

                // SEZIONE FOTO PROFILO
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // AVATAR CON INIZIALE
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8FA3B8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userInitial.toString(),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // ICONA FOTOCAMERA
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit photo",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(top = 8.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }

                // NOME UTENTE
                Text(
                    text = userName,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 16.dp)
                )

                // BIO
                if (userBio.isNotEmpty() && notEditingField) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = userBio,
                            fontSize = 14.sp,
                            color = Color.Black,
                            lineHeight = 20.sp,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifica bio",
                            tint = AppColors.PrimaryGreen,
                            modifier = Modifier
                                .size(24.dp) // Aumentato a 24.dp per rendere l'area di tocco più confortevole
                                .padding(top = 2.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(
                                        bounded = false,
                                        radius = 20.dp
                                    ) // Effetto feedback visivo circolare
                                ) {
                                    notEditingField = false
                                }
                        )
                    }
                } else  BioInput(
                    onDiscard = {
                        // Quando l'utente clicca fuori, rimetti lo stato iniziale senza salvare
                        notEditingField = true
                    }
                )

                // SEZIONE "Personal Info"
                Text(
                    text = "Personal Info",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 16.dp)
                )

                // CAMPO NOME
                ProfileInfoRow(
                    label = "Nome",
                    value = userName,
                    field = "name"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO COGNOME
                ProfileInfoRow(
                    label = "Cognome",
                    value = userSurname,
                    field = "surname"

                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO EMAIL
                ProfileInfoRow(
                    label = "Email",
                    value = userEmail,
                    field = "email"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO TELEFONO
                ProfileInfoRow(
                    label = "Mobile Phone",
                    value = userPhone,
                    field = "phone"
                )


                Spacer(modifier = Modifier.height(52.dp))

                // BOTTONE LOG OUT
                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.PrimaryGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Log Out",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}