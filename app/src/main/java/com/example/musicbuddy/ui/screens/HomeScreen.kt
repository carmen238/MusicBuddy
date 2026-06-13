package com.example.musicbuddy.ui.screens

import ImageCard
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicbuddy.ui.auth.AuthViewModel
import com.example.musicbuddy.ui.components.*
import com.example.musicbuddy.ui.theme.AppColors
import com.example.musicbuddy.ui.viewmodels.*
import com.example.musicbuddy.R

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToTuner: () -> Unit = {},
    onNavigateToDiscover: () -> Unit = {}
) {
    val context = LocalContext.current
    val userData by authViewModel.userData.collectAsState()

    val allUsersData by authViewModel.allUsersInfos.collectAsState()
    val genresStats by authViewModel.genreStatsState.collectAsState()
    val instrumentsStats by authViewModel.instrumentsStatsState.collectAsState()

    // ViewModels
    val locationViewModel: LocationViewModel = viewModel()

    // States
    val locationState by locationViewModel.locationState.collectAsState()
    val userLocation by locationViewModel.userLocation.collectAsState()
    val nearbyMusicians by locationViewModel.nearbyMusicians.collectAsState()

    // User data
    val userName = userData?.get("name") as? String ?: "Utente"
    val userGenre = userData?.get("genre").toString() ?: "Non specificato"
    val userInstrument = userData?.get("instrument") as? String ?: "Non specificato"
    val userExperience = userData?.get("experienceLevel") as? String ?: "Non specificato"
    //val userIsInBand = (userData?.get("isInBand") ?: "false") as Boolean
    val userIsInBand = userData?.get("isInBand") as? Boolean ?: false
    val currentPhotoUrl = userData?.get("photo_url") as? String
    var totalUsers = 0

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationViewModel.requestCurrentLocation(context)
        }
    }

    // Initialize
    LaunchedEffect(Unit) {
        authViewModel.fetchUserData()

        authViewModel.getAllUsersInfos()
        authViewModel.getGenresStats()
        authViewModel.getInstrumentsStats()

        locationViewModel.initializeLocationClient(context)

        // Request location permission
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationViewModel.requestCurrentLocation(context)
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Fetch nearby musicians when location is available
    LaunchedEffect(userLocation) {
        userLocation?.let {
            locationViewModel.fetchNearbyMusicians(it.latitude, it.longitude)
        }
    }

    // Community statistics data - Refined colors
    val communityGenreData = mutableListOf<BarChartData>()
    val communityInstrumentData = mutableListOf<BarChartData>()

    if(allUsersData.isNotEmpty()) totalUsers = allUsersData.size
    //METTERE SEMPRE isNotEmpty() PERCHé LA LISTA PARTE VUOTA E SI RIEMPE ASINCRONAMENTE

    genresStats.forEachIndexed { i, genreObj ->
        //visto che lista è già ordinata dal server, l'elemento in prima posizione è il più popolare
        val genreChartRow = BarChartData(genreObj.genre, genreObj.total.toFloat(), if(i==0) AppColors.PrimaryGreen else Color(0xFF9CA3AF))
        communityGenreData.add(genreChartRow)
    }

    instrumentsStats.forEachIndexed { i, instrumentObj ->
        val instrumentChartRow = BarChartData(instrumentObj.instrument, instrumentObj.total.toFloat(), if(i==0) AppColors.PrimaryGreen else Color(0xFF9CA3AF))
        communityInstrumentData.add(instrumentChartRow)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.LightBackground
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ================= HEADER =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "MusicBuddy",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.PrimaryGreen
                    )
                    Text(
                        "Welcome, $userName",
                        fontSize = 14.sp,
                        color = AppColors.LightText,
                        fontWeight = FontWeight.Normal
                    )
                }

                // User avatar
                if (currentPhotoUrl != null) {
                    val fullPhotoUrl = "http://192.168.1.100:3000$currentPhotoUrl"
                    AsyncImage(
                        model = fullPhotoUrl,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = AppColors.PrimaryGreen,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = AppColors.PrimaryGreen,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            userName.firstOrNull()?.toString() ?: "U",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // ================= YOUR PROFILE SECTION =================
            Text(
                "Your musical profile",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.DarkText,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            ImageCard()
            // Profile cards grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Instrument card
                RefinedProfileCard(
                    label = "Instrument",
                    value = userInstrument,
                    modifier = Modifier.weight(1f)
                )

                // Genre card
                RefinedProfileCard(
                    label = "Genre",
                    value = userGenre,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Experience card
                RefinedProfileCard(
                    label = "Experience",
                    value = userExperience,
                    modifier = Modifier.weight(1f)
                )

                // Band card
                RefinedProfileCard(
                    label = "Band",
                    value = if (userIsInBand) "Sì" else "No",
                    modifier = Modifier.weight(1f)
                )
            }

            // ================= COMMUNITY STATISTICS =================
            Text(
                "Community statistics",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.DarkText,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Genre chart
            HorizontalBarChart(
                data = communityGenreData,
                title = "Most popular genres",
                maxValue = totalUsers.toFloat(),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Instrument chart
            HorizontalBarChart(
                data = communityInstrumentData,
                title = "Most played instruments",
                maxValue = totalUsers.toFloat(),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // ================= LOCATION SECTION =================
            /*Text(
                "Nearby musicians",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.DarkText,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            when (locationState) {
                is LocationState.Success -> {
                    userLocation?.let {
                        // Show map
                        MusicianMapView(
                            userLatitude = it.latitude,
                            userLongitude = it.longitude,
                            nearbyMusicians = nearbyMusicians,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Show list
                        NearbyMusiciansList(
                            musicians = nearbyMusicians,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }

                is LocationState.Loading -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = AppColors.PrimaryGreen
                            )
                        }
                    }
                }

                is LocationState.PermissionDenied -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = AppColors.LightText,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Enable the localization permissions",
                                color = AppColors.LightText,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                else -> {}
            }*/

            // ================= ACTION BUTTONS =================
            //AGGIUNGERE QUI TASTO LARGO PER ANDARA ALLA MAPPA/DISCOVER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tuner button
                Button(
                    onClick = onNavigateToTuner,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.PrimaryGreen
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.diapason),
                        contentDescription = "Diapason icon",
                        modifier = Modifier.size(16.dp)
                    )
                    Text("Tuner", color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                // Discover button
                Button(
                    onClick = onNavigateToDiscover,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.AccentYellow
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = AppColors.LightText,
                        modifier = Modifier.size(18.dp)
                    )

                    Text("Friends", color = AppColors.DarkText, fontWeight = FontWeight.SemiBold)
                }

                // Profile button
                Button(
                    onClick = onNavigateToProfile,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.material3.ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = AppColors.LightText,
                        modifier = Modifier.size(18.dp)
                    )

                    Text("Profile", color = AppColors.DarkText, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ================= REFINED PROFILE CARD =================

@Composable
private fun RefinedProfileCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                label,
                fontSize = 12.sp,
                color = AppColors.LightText,
                fontWeight = FontWeight.Medium
            )
            Text(
                value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.DarkText,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenTest() {
    HomeScreen(authViewModel = AuthViewModel(), {}, {}, {})
}