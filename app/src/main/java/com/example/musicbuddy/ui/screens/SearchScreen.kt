package com.example.musicbuddy.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicbuddy.ui.auth.AuthViewModel
import com.example.musicbuddy.ui.theme.AppColors
import com.example.musicbuddy.ui.viewmodels.*
import com.example.musicbuddy.ui.components.*

@Composable
fun SearchScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val userData by authViewModel.userData.collectAsState()

    // ViewModels
    val locationViewModel: LocationViewModel = viewModel()

    // States
    val locationState by locationViewModel.locationState.collectAsState()
    val userLocation by locationViewModel.userLocation.collectAsState()
    val nearbyMusicians by locationViewModel.nearbyMusicians.collectAsState()

    // User data
    val userId = userData?.get("userId") as? Int
    val userName = userData?.get("name") as? String ?: "Utente"
    val userGenre = userData?.get("genre").toString() ?: "Non specificato"
    val userInstrument = userData?.get("instrument") as? String ?: "Non specificato"
    val userExperience = userData?.get("experienceLevel") as? String ?: "Non specificato"
    val userIsInBand = userData?.get("isInBand") as? Boolean ?: false
    val currentPhotoUrl = userData?.get("photo_url") as? String

    var musiciansLoaded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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
        println("QUI 1")
        locationViewModel.initializeLocationClient(context)

        // Request location permission
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationViewModel.requestCurrentLocation(context)
            println("QUI 2")
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Fetch nearby musicians when location is available
    LaunchedEffect(userLocation) {
        userLocation?.let {
            println("LOCATION " + it.latitude + it.longitude)
            locationViewModel.updateUserLocation(
                userId as Int,
                it.latitude,
                it.longitude
            )
        }
        userLocation?.let {
            locationViewModel.fetchNearbyMusicians(userId as Int, it.latitude, it.longitude)
        }
    }

    if(nearbyMusicians.isNotEmpty()) musiciansLoaded = true

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.LightBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.LightBackground),
            verticalArrangement = Arrangement.Center,
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

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    "Nearby musicians",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.DarkText
                )
            }

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (locationState) {
                    is LocationState.Success -> {
                        userLocation?.let {
                            // Show map
                            if(musiciansLoaded) {
                                MusicianMapView(
                                    userLatitude = it.latitude,
                                    userLongitude = it.longitude,
                                    nearbyMusicians = nearbyMusicians,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                // Show list
                                NearbyMusiciansList(
                                    userId as Int,
                                    userLatitude = it.latitude,
                                    userLongitude = it.longitude,
                                    musicians = nearbyMusicians,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    locationViewModel
                                )
                            }
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
                }
            }
        }
    }
}