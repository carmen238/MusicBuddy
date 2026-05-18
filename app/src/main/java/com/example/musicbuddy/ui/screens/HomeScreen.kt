package com.example.musicbuddy.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicbuddy.ui.theme.AppColors
import com.example.musicbuddy.R

@Composable
fun HomeScreen(onNavigateToSearch: () -> Unit,
               onNavigateToProfile: () -> Unit,
               onNavigateToTuner: () -> Unit,
               onNavigateToChat: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.LightBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(84.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(verticalAlignment = Alignment.Top) {
                AsyncImage(
                    model = "file:///android_asset/eighth_note_sketch.jpg",
                    contentDescription = "Stylized eighth note",
                    modifier = Modifier.height(70.dp).width(52.dp),
                    //contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Croma",
                    fontSize = 68.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Cursive,
                    color = AppColors.PrimaryGreen
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- TASTO SEARCH (GRANDE) ---
            Button(
                onClick = onNavigateToSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF84AAE3),             // Colore dello sfondo (solitamente trasparente)
                    contentColor = darkText,           // Colore del testo e dell'icona
                    disabledContentColor = Color.Gray   // Colore quando il bottone è disabilitato
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place, // Icona mappa
                        contentDescription = "Map",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "SEARCH",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- GRIGLIA ALTRI BOTTONI ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Bottone Profilo
                HomeSmallButton(
                    text = "Profile",
                    icon = Icons.Default.Person,
                    color = Color(0xFFFFFB7A),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToProfile
                )
                // Bottone Tuner
                HomeSmallButton(
                    text = "Tuner",
                    icon = Icons.Default.Person, // Icona simile a un tuner
                    isTuner = true,
                    color = Color(0xFF91D64F),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTuner
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottone Chat (Largo quanto la griglia sopra)
            HomeSmallButton(
                text = "Active Chats",
                icon = Icons.Default.Chat,
                color = Color(0xFFFFBB3D),
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToChat
            )
        }
    }
}

@Composable
fun HomeSmallButton(
    text: String,
    icon: ImageVector,
    isTuner: Boolean = false,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = color,             // Colore dello sfondo (solitamente trasparente)
            contentColor = darkText,           // Colore del testo e dell'icona
            disabledContentColor = Color.Gray   // Colore quando il bottone è disabilitato
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if(isTuner) {
                Icon(
                    painter = painterResource(id = R.drawable.diapason),
                    contentDescription = text,
                    modifier = Modifier.size(24.dp)
                )
            }
            else {
                Icon(imageVector = icon, contentDescription = text)
            }
            Text(text = text, fontSize = 14.sp)
        }
    }
}

@Preview
@Composable
fun HomeScreenTest() {
    HomeScreen(onNavigateToSearch = {}, onNavigateToProfile = {}, onNavigateToTuner = {}, onNavigateToChat = {})
}