package com.example.musicbuddy.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.musicbuddy.ui.auth.PhotoUploadState
import com.example.musicbuddy.ui.auth.PhotoViewModel

/**
 * PhotoPickerButton - Button to pick or take a photo
 */
@Composable
fun PhotoPickerButton(
    photoViewModel: PhotoViewModel,
    userId: String,
    currentPhotoUrl: String? = null,
    onPhotoSelected: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val photoUploadState by photoViewModel.photoUploadState.collectAsState()
    val photoUrl by photoViewModel.photoUrl.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            photoViewModel.uploadPhoto(context, uri, userId)
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            // Save bitmap to temporary file and upload
            val tempFile =
                java.io.File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
            java.io.FileOutputStream(tempFile).use { output ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, output)
            }

            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )
            photoViewModel.uploadPhoto(context, uri, userId)
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    // Update parent when photo is uploaded
    LaunchedEffect(photoUrl) {
        photoUrl?.let { onPhotoSelected(it) }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Photo Display
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Gray)
                .clickable { showDialog = true },
            contentAlignment = Alignment.Center
        ) {
            if (photoUrl != null || currentPhotoUrl != null) {
                val fullPhotoUrl = if (photoUrl != null) {
                    "http://192.168.1.100:3000${photoUrl}"  // ✅ Sostituisci con il tuo IP!
                } else {
                    "http://192.168.1.100:3000${currentPhotoUrl}"
                }

                AsyncImage(
                    model = fullPhotoUrl,
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit,
//                    contentAlignment = Alignment.Center
                )

            } else {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Add Photo",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Loading indicator
            if (photoUploadState is PhotoUploadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center),
                    color = Color.White
                )
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        // Upload status
        when (photoUploadState) {
            is PhotoUploadState.Success -> {
                Text(
                    "✅ Photo uploaded",
                    color = Color.Green,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            is PhotoUploadState.Error -> {
                Text(
                    "❌ ${(photoUploadState as PhotoUploadState.Error).message}",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action button
        Button(
            onClick = { showDialog = true },
            enabled = photoUploadState !is PhotoUploadState.Loading
        ) {
            Text(if (photoUrl != null || currentPhotoUrl != null) "Change Photo" else "Add Photo")
        }
    }

    // Photo selection dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Choose Photo Source") },
            text = { Text("Select where to get your photo from") },
            confirmButton = {
                Button(
                    onClick = {
                        galleryLauncher.launch("image/*")
                        showDialog = false
                    }
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gallery")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Check camera permission
                        val hasCameraPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasCameraPermission) {
                            cameraLauncher.launch(null)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                        showDialog = false
                    }
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Camera")
                }
            }
        )
    }
}