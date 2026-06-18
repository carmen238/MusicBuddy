package com.example.musicbuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.musicbuddy.data.models.NearbyMusicianInfo
import com.example.musicbuddy.ui.theme.AppColors
import com.example.musicbuddy.ui.viewmodels.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Google Maps composable showing nearby musicians
 */
@Composable
fun MusicianMapView(
    userLatitude: Double,
    userLongitude: Double,
    nearbyMusicians: List<NearbyMusicianInfo>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AppColors.LightBackground,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        // Title
        /*Text(
            "🎵 Musicisti Vicini",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.PrimaryGreen,
            modifier = Modifier.padding(bottom = 12.dp)
        )*/

        // Map
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    onCreate(null)
                    getMapAsync { googleMap ->
                        // Set user location
                        val userLocation = LatLng(userLatitude, userLongitude)
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(userLocation)
                                .title("You are here")
                                .snippet("Your position")
                        )

                        // Add nearby musicians
                        nearbyMusicians.forEach { musician ->
                            val musicianLocation = LatLng(musician.latitude, musician.longitude)
                            val distance = calculateDistance(userLatitude, userLongitude, musician.latitude, musician.longitude)
                            println("MUSICIAN: " + musician.latitude + " " + musician.longitude)
                            googleMap.addMarker(
                                MarkerOptions()
                                    .position(musicianLocation)
                                    .title(musician.name)
                                    .snippet("${musician.instrument} - ${String.format("%.1f", distance)} km")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            )
                        }

                        // Center map on user
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(userLocation, 14f)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .background(
                    color = AppColors.InputBackground,
                    shape = RoundedCornerShape(8.dp)
                )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Musicians list
        if (nearbyMusicians.isNotEmpty()) {
            Text(
                "${nearbyMusicians.size} musicians found",
                fontSize = 12.sp,
                color = AppColors.LightText,
                fontWeight = FontWeight.Medium
            )
        } else {
            Text(
                "No nearby musicians found",
                fontSize = 12.sp,
                color = AppColors.LightText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * List of nearby musicians
 */
@Composable
fun NearbyMusiciansList(
    userId: Int,
    userLatitude: Double,
    userLongitude: Double,
    musicians: List<NearbyMusicianInfo>,
    modifier: Modifier = Modifier,
    locationViewModel: LocationViewModel
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AppColors.LightBackground,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        // Title
        Text(
            "🎸 Nearby musicians",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.PrimaryGreen,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (musicians.isEmpty()) {
            Text(
                "No nearby musicians found",
                fontSize = 13.sp,
                color = AppColors.LightText,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            musicians.forEach { musician ->
                val distance = calculateDistance(userLatitude, userLongitude, musician.latitude, musician.longitude)
                MusicianListItem(userId, musician, distance, locationViewModel)
                if (musician != musicians.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Single musician list item
 */
@Composable
private fun MusicianListItem(userId: Int, musician: NearbyMusicianInfo, distance: Double, locationViewModel: LocationViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AppColors.InputBackground,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
            .clickable { showDialog = true },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (musician.photo_url != "") {
            AsyncImage(
                model = musician.photo_url,
                contentDescription = "Profile photo",
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Crop
            )

        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                musician.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.DarkText
            )
            Text(
                "Instrument: "+musician.instrument,
                fontSize = 14.sp,
                color = AppColors.LightText
            )
            Text(
                "Genre: "+musician.genre,
                fontSize = 14.sp,
                color = AppColors.LightText
            )
            Text(
                "Experience: "+musician.experienceLevel,
                fontSize = 14.sp,
                color = AppColors.LightText
            )
            Text(
                "Band: " + if(musician.isInBand==1) "Yes" else "No",
                fontSize = 14.sp,
                color = AppColors.LightText
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = AppColors.PrimaryGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = AppColors.PrimaryGreen,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "${String.format("%.1f", distance)} km",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.PrimaryGreen
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    // Si attiva quando l'utente tocca fuori dal popup o preme "Indietro"
                    showDialog = false
                },
                title = {Text(text = "Send friend request?") },
                text = {Text(text = "Do you want to send a friend request to this musician?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            locationViewModel.sendFriendRequest(userId, musician.id)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.PrimaryGreen,  // Background color
                            contentColor = Color.White    // Text color
                        )
                    ) {
                        Text("Send")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false }, // Chiude il popup
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,  // Background color
                            contentColor = Color.White    // Text color
                        )
                    ) {
                        Text("Back")
                    }
                }
            )
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