package com.example.musicbuddy.ui.screens

import androidx.compose.runtime.Composable
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.musicbuddy.ui.auth.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GPSScreen(viewModel: MapViewModel) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Gestione dei permessi con Accompanist
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Richiede i permessi all'avvio
    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    if (permissionState.allPermissionsGranted) {
        // Recupera l'ultima posizione GPS nota del dispositivo
        @SuppressLint("MissingPermission")
        LaunchedEffect(Unit) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    viewModel.updateLocationAndFetchMusicians(currentLatLng, radiusInKm = 10)
                }
            }
        }

        val userLocation by viewModel.currentUserLocation
        val musicians by viewModel.nearbyMusicians

        if (userLocation != null) {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(userLocation!!, 14f)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true) // Mostra il pallino blu dell'utente
                ) {
                    // Genera un Marker sulla mappa per ogni musicista nel raggio d'azione
                    musicians.forEach { musician ->
                        Marker(
                            state = MarkerState(position = musician.location),
                            title = musician.name,
                            snippet = "Strumento: ${musician.instrument}"
                        )
                    }
                }
            }
        } else {
            // Schermata di caricamento in attesa del fix del GPS
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ricerca della tua posizione GPS in corso...")
            }
        }
    } else {
        // Schermata di errore se l'utente nega l'accesso al GPS
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("L'applicazione richiede i permessi GPS per mostrare i musicisti nelle vicinanze.")
        }
    }
}