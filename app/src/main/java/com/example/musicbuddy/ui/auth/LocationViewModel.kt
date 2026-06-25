package com.example.musicbuddy.ui.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicbuddy.data.models.DeleteUserRequest
import com.example.musicbuddy.data.models.*
import com.example.musicbuddy.network.RetrofitClient
import com.example.musicbuddy.ui.auth.AuthState
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.http.Body
import kotlin.Int

// ============= DATA CLASSES =============

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float
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
    data class Success(val musicians: List<NearbyMusicianInfo>) : NearbyMusiciansState()
    data class Error(val message: String) : NearbyMusiciansState()
}

// ============= LOCATION VIEW MODEL =============

class LocationViewModel : ViewModel() {

    private val authApiService = RetrofitClient.getAuthApiService()
    private val _locationState = MutableStateFlow<LocationState>(LocationState.Idle)
    val locationState: StateFlow<LocationState> = _locationState

    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation

    private val _nearbyMusiciansState = MutableStateFlow<NearbyMusiciansState>(NearbyMusiciansState.Idle)
    val nearbyMusiciansState: StateFlow<NearbyMusiciansState> = _nearbyMusiciansState

    private val _nearbyMusicians = MutableStateFlow<List<NearbyMusicianInfo>>(emptyList())
    val nearbyMusicians: StateFlow<List<NearbyMusicianInfo>> = _nearbyMusicians

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

                // 1. Prova a prendere l'ultima posizione conosciuta (rapida, dalla cache)
                var location = fusedLocationClient?.lastLocation?.await()

                // 2. SE LA CACHE È VUOTA, forza una richiesta in tempo reale
                if (location == null) {
                    Log.d("LocationViewModel", "Cache location is null, requesting fresh location...")

                    //withContext(NonCancellable) impedisce il crash "Job was cancelled" se l'interfaccia si aggiorna
                    location = withContext(NonCancellable) {
                        // Creiamo una richiesta esplicita con un timeout di 5 secondi (5000 ms)
                        val locationRequest = CurrentLocationRequest.Builder()
                            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY) // Usa reti Wi-Fi/Celle se il GPS puro fatica
                            .setMaxUpdateAgeMillis(60000) // Accetta posizioni vecchie fino a un minuto
                            .setDurationMillis(5000)      // IMPORTANTE: Se entro 5 secondi non risponde, sblocca la chiamata
                            .build()

                        fusedLocationClient?.getCurrentLocation(
                            locationRequest,
                            com.google.android.gms.tasks.CancellationTokenSource().token
                        )?.await()
                    }
                }

                // 3. Gestione del risultato finale
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
                    // Se è ancora null (es. il GPS è spento del tutto a livello hardware)
                    _locationState.value = LocationState.Error("Location not available")
                    Log.w("LocationViewModel", "Location is still null after fresh request")
                }

            } catch (e: Exception) {
                _locationState.value = LocationState.Error(e.message ?: "Unknown error")
                Log.e("LocationViewModel", "Error getting location: ${e.message}")
            }
        }
    }

    /**
     * Post user location in the backend database
     */
    fun updateUserLocation(
        userId: Int,
        userLatitude: Double,
        userLongitude: Double
    ) {
        viewModelScope.launch {
            try {
                val postLocationRequest = UpdateLatLongRequest(userId, userLatitude, userLongitude)

                val response = authApiService.postUserLocation(postLocationRequest)

                if(!response.success) Log.e("AuthViewModel", "Error during account deletion on server side: ${response.message}")

                // 3. Log per debug
                Log.d("AuthViewModel", "Location updated")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error during location update: ${e.message}")
            }
        }
    }

    /**
     * Fetch nearby musicians from backend
     * This should be called after getting the user's location
     */
    fun fetchNearbyMusicians(
        userId: Int,
        userLatitude: Double,
        userLongitude: Double,
        range: Double = 10.0
    ) {
        viewModelScope.launch {
            try {
                _nearbyMusiciansState.value = NearbyMusiciansState.Loading

                //VISTO CHE NON DIAMO LA POSSIBILITà DI SCEGLIERE IL RANGE, PER ORA LO LASCIAMO A 10 KM DI DEFAULT (POSSIBILE SCELTA IN FUTURO)
                val getNearbyMusiciansRequest = GetNearbyMusiciansRequest(userId, userLatitude, userLongitude, range)

                val response = authApiService.getNearbyMusicians(getNearbyMusiciansRequest)

                if(!response.success) Log.e("AuthViewModel", "Error during fetching nearby musicians: ${response.message}")

                _nearbyMusicians.value = response.data
                _nearbyMusiciansState.value = NearbyMusiciansState.Success(response.data)

                Log.d("LocationViewModel", "Found ${response.data.size} nearby musicians")

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