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
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImage
import com.example.musicbuddy.ui.auth.AuthState
import com.example.musicbuddy.ui.auth.AuthViewModel
import com.example.musicbuddy.ui.components.SignUpTextField
import com.example.musicbuddy.ui.components.Validators
import com.example.musicbuddy.ui.components.DropdownMenuField
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
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    //SPOSTARE QUESTE VARIABILI IN AuthViewModel COSÌ CHE I DATI SONO VISIBILI A TUTTE LE SCHERMATE MULTIPLE DEL SIGN UP
    //(AGGIUNGENDO ANCHE NUOVI CAMPI DELLA SECONDA PAGINA)
    //COSì I DATI DEL LOGIN SARANNO SALVATI DA GOOGLE (FIREBASE) E I DATI AGGIUNTIVI CE LI SALVIAMO IN UN NOSTRO DATABASE SQL
    //INOLTRE, VISTO CHE FIREBASE SALVA SOLO EMAIL E PASSWORD, GLI ALTRI DATI LI USIAMO NOI NEL NOSTRO DATABASE
    /*var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }*/

    // Osserva lo stato di autenticazione
    val authState by authViewModel.authState.collectAsState()

    // Aggiorna il messaggio di errore quando lo stato cambia
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Error -> {
                authViewModel.errorMessage = (authState as AuthState.Error).message
                authViewModel.showError = true

                launch {
                    delay(3000L)
                    authViewModel.showError = false
                }
            }
            is AuthState.Authenticated -> {
                authViewModel.showError = false
            }
            else -> {
                authViewModel.showError = false
            }
        }
    }

    // Validazione globale
    val isFormValid =
        Validators.isValidName(authViewModel.name) &&
                Validators.isValidName(authViewModel.surname) &&
                Validators.isValidPhone(authViewModel.phone) &&
                Validators.isValidEmail(authViewModel.email) &&
                Validators.isValidPassword(authViewModel.password) &&
                Validators.verifyConfirmPassword(authViewModel.password, authViewModel.confirmPassword)

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
                    value = authViewModel.name,
                    onValueChange = { authViewModel.name = it },
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
                    value = authViewModel.surname,
                    onValueChange = { authViewModel.surname = it },
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
                    value = authViewModel.phone,
                    onValueChange = { authViewModel.phone = it },
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
                    value = authViewModel.email,
                    onValueChange = { authViewModel.email = it },
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
                    value = authViewModel.password,
                    onValueChange = { authViewModel.password = it },
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
                    value = authViewModel.confirmPassword,
                    onValueChange = { authViewModel.confirmPassword = it },
                    label = "Confirm password",
                    placeholder = "Confirm your password",
                    inputBackground = AppColors.InputBackground,
                    hintColor = AppColors.HintText,
                    textColor = AppColors.DarkText,
                    isPassword = true,
                    accentColor = AppColors.AccentYellow,
                    validator = { Validators.verifyConfirmPassword(authViewModel.password, authViewModel.confirmPassword) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // MESSAGGIO DI ERRORE
                if (authViewModel.showError) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Text(
                            text = authViewModel.errorMessage,
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
                        onContinueClick()
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

@Composable
fun SignUpScreen2(
    authViewModel: AuthViewModel,
    onCreateClick: (name: String, surname: String, phone: String, email: String, password: String, playedInstrument: String, favoriteMusicGenre: String, favoriteMusicSubgenre: String, currentFavoriteBand: String, inABand: Boolean, profilePhoto: ByteArray) -> Unit,
    onBackClick: () -> Unit = {}
) {
    //STESSA COSA DI SOPRA
    /*var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }*/

    // Osserva lo stato di autenticazione
    val authState by authViewModel.authState.collectAsState()

    // Aggiorna il messaggio di errore quando lo stato cambia
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Error -> {
                authViewModel.errorMessage = (authState as AuthState.Error).message
                authViewModel.showError = true

                launch {
                    delay(3000L)
                    authViewModel.showError = false
                }
            }
            is AuthState.Authenticated -> {
                authViewModel.showError = false
            }
            else -> {
                authViewModel.showError = false
            }
        }
    }

    // Validazione globale
    val isFormValid =
        Validators.isValidName(authViewModel.playedInstrument) && Validators.isValidName(authViewModel.currentFavoriteBand)

    //Dropdown menu for music genres
    val dropdownItems1 = listOf("Classical", "Pop", "Hip-Pop", "Rock", "Blues", "Folk/Traditional", "Jazz", "Metal", "Punk", "Electronic", "R&B/Soul")
    val dropdownItems2 = listOf("Yes", "No")

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
                    text = "Sign Up - step 2",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.DarkText,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(20.dp))

                SignUpTextField(
                    value = authViewModel.playedInstrument,
                    onValueChange = { authViewModel.playedInstrument = it },
                    label = "Music instrument that you play",
                    placeholder = "Music instrument",
                    inputBackground = AppColors.InputBackground,
                    hintColor = AppColors.HintText,
                    textColor = AppColors.DarkText,
                    accentColor = AppColors.AccentYellow,
                    validator = { Validators.isValidName(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                DropdownMenuField(
                    options = dropdownItems1,
                    label = "Favorite music genre",
                    placeholder = "Music genre",
                    onOptionSelected = { choice ->
                        authViewModel.favoriteMusicGenre = choice
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SignUpTextField(
                    value = authViewModel.favoriteMusicSubgenre,
                    onValueChange = { authViewModel.favoriteMusicSubgenre = it },
                    label = "Favorite music sub-genre (if any)",
                    placeholder = "Music sub-genre",
                    inputBackground = AppColors.InputBackground,
                    hintColor = AppColors.HintText,
                    textColor = AppColors.DarkText,
                    accentColor = AppColors.AccentYellow,
                    //No validator, this field can be empty
                )

                Spacer(modifier = Modifier.height(12.dp))

                SignUpTextField(
                    value = authViewModel.currentFavoriteBand,
                    onValueChange = { authViewModel.currentFavoriteBand = it },
                    label = "Current favorite band or singer",
                    placeholder = "Band/Singer",
                    inputBackground = AppColors.InputBackground,
                    hintColor = AppColors.HintText,
                    textColor = AppColors.DarkText,
                    accentColor = AppColors.AccentYellow,
                    validator = { Validators.isValidName(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                DropdownMenuField(
                    options = dropdownItems2,
                    label = "Are you currently in a band?",
                    placeholder = "Yes/No",
                    onOptionSelected = { choice ->
                        if(choice == "Yes") authViewModel.inABand = true
                        else authViewModel.inABand = false
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                //LA FOTO DEL PROFILO LA FACCIAMO CARICARE DOPO DALLA PAGINA DEL PROFILO

                // MESSAGGIO DI ERRORE
                if (authViewModel.showError) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Text(
                            text = authViewModel.errorMessage,
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

                // BOTTONE CREATE ACCOUNT
                Button(
                    onClick = {
                        onCreateClick(authViewModel.name, authViewModel.surname, authViewModel.phone, authViewModel.email, authViewModel.password, authViewModel.playedInstrument, authViewModel.favoriteMusicGenre, authViewModel.favoriteMusicSubgenre, authViewModel.currentFavoriteBand, authViewModel.inABand, authViewModel.profilePhoto)
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
                        text = "Create account",
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