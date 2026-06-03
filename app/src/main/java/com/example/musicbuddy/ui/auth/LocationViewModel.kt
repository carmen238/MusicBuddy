package com.example.musicbuddy.ui.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ============= DATA CLASSES =============

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float
)

data class NearbyMusician(
    val userId: String,
    val name: String,
    val instrument: String,
    val latitude: Double,
    val longitude: Double,
    val distance: Double, // in km
    val profilePhotoUrl: String? = null
)

// ============= LOCATION STATE =============

sealed class LocationState {
    object Idle : LocationState()
    object Loading : LocationState()
    data class Success(val location: UserLocation) : LocationState()
    data class Error(val message: String) : LocationState()
    object PermissionDenied : LocationState()
}

sealed class NearbyMusiciansState {
    object Idle : NearbyMusiciansState()
    object Loading : NearbyMusiciansState()
    data class Success(val musicians: List<NearbyMusician>) : NearbyMusiciansState()
    data class Error(val message: String) : NearbyMusiciansState()
}

// ============= LOCATION VIEW MODEL =============

class LocationViewModel : ViewModel() {

    private val _locationState = MutableStateFlow<LocationState>(LocationState.Idle)
    val locationState: StateFlow<LocationState> = _locationState

    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation

    private val _nearbyMusiciansState = MutableStateFlow<NearbyMusiciansState>(NearbyMusiciansState.Idle)
    val nearbyMusiciansState: StateFlow<NearbyMusiciansState> = _nearbyMusiciansState

    private val _nearbyMusicians = MutableStateFlow<List<NearbyMusician>>(emptyList())
    val nearbyMusicians: StateFlow<List<NearbyMusician>> = _nearbyMusicians

    private var fusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Initialize location client
     */
    fun initializeLocationClient(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    /**
     * Request current location
     */
    fun requestCurrentLocation(context: Context) {
        viewModelScope.launch {
            try {
                // Check permissions
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    _locationState.value = LocationState.PermissionDenied
                    Log.w("LocationViewModel", "Location permission denied")
                    return@launch
                }

                _locationState.value = LocationState.Loading

                val location = fusedLocationClient?.lastLocation?.await()

                if (location != null) {
                    val userLocation = UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy
                    )
                    _userLocation.value = userLocation
                    _locationState.value = LocationState.Success(userLocation)
                    Log.d("LocationViewModel", "Location obtained: ${location.latitude}, ${location.longitude}")
                } else {
                    _locationState.value = LocationState.Error("Location not available")
                    Log.w("LocationViewModel", "Location is null")
                }

            } catch (e: Exception) {
                _locationState.value = LocationState.Error(e.message ?: "Unknown error")
                Log.e("LocationViewModel", "Error getting location: ${e.message}")
            }
        }
    }

    /**
     * Calculate distance between two coordinates (Haversine formula)
     */
    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val R = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }

    /**
     * Fetch nearby musicians from backend
     * This should be called after getting the user's location
     */
    fun fetchNearbyMusicians(
        userLatitude: Double,
        userLongitude: Double,
        radiusKm: Double = 10.0
    ) {
        viewModelScope.launch {
            try {
                _nearbyMusiciansState.value = NearbyMusiciansState.Loading

                // TODO: Replace with actual API call to your backend
                // For now, we'll use mock data
                val mockMusicians = listOf(
                    NearbyMusician(
                        userId = "user_1",
                        name = "Marco Rossi",
                        instrument = "Chitarra",
                        latitude = userLatitude + 0.01,
                        longitude = userLongitude + 0.01,
                        distance = calculateDistance(
                            userLatitude,
                            userLongitude,
                            userLatitude + 0.01,
                            userLongitude + 0.01
                        )
                    ),
                    NearbyMusician(
                        userId = "user_2",
                        name = "Giulia Bianchi",
                        instrument = "Voce",
                        latitude = userLatitude - 0.02,
                        longitude = userLongitude + 0.015,
                        distance = calculateDistance(
                            userLatitude,
                            userLongitude,
                            userLatitude - 0.02,
                            userLongitude + 0.015
                        )
                    ),
                    NearbyMusician(
                        userId = "user_3",
                        name = "Luca Verdi",
                        instrument = "Batteria",
                        latitude = userLatitude + 0.015,
                        longitude = userLongitude - 0.02,
                        distance = calculateDistance(
                            userLatitude,
                            userLongitude,
                            userLatitude + 0.015,
                            userLongitude - 0.02
                        )
                    )
                )

                // Filter by radius
                val filtered = mockMusicians.filter { it.distance <= radiusKm }
                    .sortedBy { it.distance }

                _nearbyMusicians.value = filtered
                _nearbyMusiciansState.value = NearbyMusiciansState.Success(filtered)

                Log.d("LocationViewModel", "Found ${filtered.size} nearby musicians")

            } catch (e: Exception) {
                _nearbyMusiciansState.value = NearbyMusiciansState.Error(e.message ?: "Unknown error")
                Log.e("LocationViewModel", "Error fetching nearby musicians: ${e.message}")
            }
        }
    }

    /**
     * Check if location permission is granted
     */
    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}