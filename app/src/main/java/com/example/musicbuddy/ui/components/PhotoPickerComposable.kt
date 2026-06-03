package com.example.musicbuddy.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.musicbuddy.ui.auth.PhotoUploadState
import com.example.musicbuddy.ui.auth.PhotoViewModel
import com.example.musicbuddy.ui.theme.AppColors

/**
 * PhotoPickerButton - Button to pick or take a photo
 * Enhanced version with AppColors palette
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
                .background(AppColors.InputBackground)
                .clickable { showDialog = true },
            contentAlignment = Alignment.Center
        ) {
            if (photoUrl != null || currentPhotoUrl != null) {
                val fullPhotoUrl = if (photoUrl != null) {
                    "http://172.20.10.4:3000${photoUrl}"  // ✅ Sostituisci con il tuo IP!
                } else {
                    "http://172.20.10.4:3000${currentPhotoUrl}"
                }

                AsyncImage(
                    model = fullPhotoUrl,
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

            } else {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Add Photo",
                    tint = AppColors.PrimaryGreen,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Loading indicator
            if (photoUploadState is PhotoUploadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center),
                    color = AppColors.PrimaryGreen,
                    trackColor = AppColors.InputBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Upload status
        when (photoUploadState) {
            is PhotoUploadState.Success -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(
                            color = AppColors.SuccessGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "✅ Foto caricata con successo",
                        color = AppColors.SuccessGreen,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            is PhotoUploadState.Error -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(
                            color = AppColors.ErrorRed.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "❌ ${(photoUploadState as PhotoUploadState.Error).message}",
                        color = AppColors.ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action button
        Button(
            onClick = { showDialog = true },
            enabled = photoUploadState !is PhotoUploadState.Loading,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(44.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.PrimaryGreen,
                disabledContainerColor = AppColors.DisabledButton
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .padding(end = 8.dp),
                tint = Color.White
            )
            Text(
                if (photoUrl != null || currentPhotoUrl != null) "Cambia Foto" else "Aggiungi Foto",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    // Photo selection dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    "Scegli la fonte",
                    color = AppColors.DarkText,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Da dove vuoi prendere la foto?",
                    color = AppColors.LightText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        galleryLauncher.launch("image/*")
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.PrimaryGreen
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 6.dp),
                        tint = Color.White
                    )
                    Text("Galleria", color = Color.White)
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
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.AccentYellow
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 6.dp),
                        tint = AppColors.DarkText
                    )
                    Text("Fotocamera", color = AppColors.DarkText)
                }
            },
            containerColor = AppColors.LightBackground,
            titleContentColor = AppColors.DarkText,
            textContentColor = AppColors.LightText,
            shape = RoundedCornerShape(12.dp)
        )
    }
}