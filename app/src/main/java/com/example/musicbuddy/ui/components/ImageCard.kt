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
            /*AsyncImage(
                model = "http://172.20.10.4:3000/uploads/profile-photos/undefined_1780492746157-590666397.jpg",
                contentDescription = "Immagine",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))*/

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