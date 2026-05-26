package com.example.musicbuddy.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

data class Musician(
    //CAMBIARE QUI
    val id: String,
    val name: String,
    val instrument: String,
    val location: LatLng
)

class MapViewModel : ViewModel() {
    // Stato della mappa e lista dei musicisti vicini
    var currentUserLocation = mutableStateOf<LatLng?>(null)
    var nearbyMusicians = mutableStateOf<List<Musician>>(emptyList())

    // Aggiorna la posizione e richiede i dati al backend
    fun updateLocationAndFetchMusicians(latLng: LatLng, radiusInKm: Int) {
        currentUserLocation.value = latLng
        fetchMusiciansFromRemote(latLng, radiusInKm)
    }

    //CAMBIARE QUI
    private fun fetchMusiciansFromRemote(location: LatLng, radius: Int) {
        // Qui invochi la tua API (es. Retrofit/Ktor) inviando:
        // location.latitude, location.longitude e radius
        // Esempio di mock dati ricevuti dal server:
        nearbyMusicians.value = listOf(
            Musician("1", "Marco", "Chitarra", LatLng(location.latitude + 0.005, location.longitude + 0.005)),
            Musician("2", "Sara", "Batteria", LatLng(location.latitude - 0.003, location.longitude - 0.002))
        )
    }
}
