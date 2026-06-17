package com.example.musicbuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.musicbuddy.ui.theme.AppColors
import com.example.musicbuddy.ui.viewmodels.NearbyMusician
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Google Maps composable showing nearby musicians
 */
@Composable
fun MusicianMapView(
    userLatitude: Double,
    userLongitude: Double,
    nearbyMusicians: List<NearbyMusician>,
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
                                .title("Tu sei qui")
                                .snippet("La tua posizione")
                        )

                        // Add nearby musicians
                        nearbyMusicians.forEach { musician ->
                            val musicianLocation = LatLng(musician.latitude, musician.longitude)
                            googleMap.addMarker(
                                MarkerOptions()
                                    .position(musicianLocation)
                                    .title(musician.name)
                                    .snippet("${musician.instrument} - ${String.format("%.1f", musician.distance)} km")
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
                .height(300.dp)
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
    musicians: List<NearbyMusician>,
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
                MusicianListItem(musician)
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
private fun MusicianListItem(musician: NearbyMusician) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AppColors.InputBackground,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                musician.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.DarkText
            )
            Text(
                musician.instrument,
                fontSize = 12.sp,
                color = AppColors.LightText
            )
            Text(
                musician.genre,
                fontSize = 12.sp,
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
                "${String.format("%.1f", musician.distance)} km",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.PrimaryGreen
            )
        }
    }
}