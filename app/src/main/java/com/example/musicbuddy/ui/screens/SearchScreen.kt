package com.example.musicbuddy.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.musicbuddy.ui.auth.FriendsViewModel
import com.example.musicbuddy.ui.theme.AppColors
import com.example.musicbuddy.ui.viewmodels.*
import com.example.musicbuddy.ui.components.*

@Composable
fun SearchScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val userData by authViewModel.userData.collectAsState()

    // ViewModels
    val locationViewModel: LocationViewModel = viewModel()
    val friendsViewModel: FriendsViewModel = viewModel()

    // States
    val locationState by locationViewModel.locationState.collectAsState()
    val userLocation by locationViewModel.userLocation.collectAsState()
    val nearbyMusicians by locationViewModel.nearbyMusicians.collectAsState()
    val nearbyMusiciansState by locationViewModel.nearbyMusiciansState.collectAsState()

    // User data
    val userId = userData?.get("userId") as? Int


    val scrollState = rememberScrollState()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            locationViewModel.requestCurrentLocation(context)
        }
    }

    // Initialize
    LaunchedEffect(Unit) {
        authViewModel.fetchUserData()
        locationViewModel.initializeLocationClient(context)

        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFine && hasCoarse) {
            locationViewModel.requestCurrentLocation(context)
        } else {
            // Richiedi entrambi i permessi insieme
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
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

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onRefreshClick,
                    modifier = Modifier
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.PrimaryGreen
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.material3.ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh button",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )

                    Text("Refresh", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, maxLines = 1, softWrap = false)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (locationState is LocationState.Success && nearbyMusiciansState is NearbyMusiciansState.Success) {
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
                            userId as Int,
                            userLatitude = it.latitude,
                            userLongitude = it.longitude,
                            musicians = nearbyMusicians,
                            modifier = Modifier.padding(bottom = 16.dp),
                            friendsViewModel
                        )
                    }
                }

                else if (locationState is LocationState.Loading || nearbyMusiciansState is NearbyMusiciansState.Loading) {
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

                else if (locationState is LocationState.PermissionDenied || nearbyMusiciansState is NearbyMusiciansState.Error) {
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
            }
        }
    }
}