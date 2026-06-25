package com.example.musicbuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun ImageCard(userBio: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Your bio",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                if(userBio != "") {
                    Text(
                        text = userBio,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else {
                    Text(
                        text = "Empty (add one in your profile settings)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}