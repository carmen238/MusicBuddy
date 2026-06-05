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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.musicbuddy.ui.auth.PhotoUploadState
import com.example.musicbuddy.ui.auth.PhotoViewModel
import com.example.musicbuddy.ui.theme.AppColors

@Composable
fun PhotoPickerButton(
    photoViewModel: PhotoViewModel,
    currentPhotoUrl: String? = null,
    onPhotoSelected: (String) -> Unit = {}
) {
    val context = LocalContext.current

    val uploadState by photoViewModel.photoUploadState.collectAsState()
    val uploadedUrl by photoViewModel.photoUrl.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    // 👉 URL finale UNIFICATO (questo è il trucco importante)
    val finalPhotoUrl = uploadedUrl ?: currentPhotoUrl

    // 👉 comunica al parent quando cambia
    LaunchedEffect(finalPhotoUrl) {
        finalPhotoUrl?.let { onPhotoSelected(it) }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            photoViewModel.uploadPhoto(context, it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val tempFile =
                java.io.File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")

            java.io.FileOutputStream(tempFile).use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
            }

            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )

            photoViewModel.uploadPhoto(context, uri)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {

        // ================= PHOTO =================
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(AppColors.InputBackground)
                .clickable { showDialog = true },
            contentAlignment = Alignment.Center
        ) {

            if (!finalPhotoUrl.isNullOrBlank()) {

                AsyncImage(
                    model = finalPhotoUrl,
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

            } else {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = AppColors.PrimaryGreen,
                    modifier = Modifier.size(40.dp)
                )
            }

            if (uploadState is PhotoUploadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(120.dp),
                    color = AppColors.PrimaryGreen
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ================= STATUS =================
        when (uploadState) {

            is PhotoUploadState.Success -> {
                Text(
                    "✅ Foto aggiornata",
                    color = AppColors.SuccessGreen
                )
            }

            is PhotoUploadState.Error -> {
                Text(
                    "❌ ${(uploadState as PhotoUploadState.Error).message}",
                    color = AppColors.ErrorRed
                )
            }

            else -> {}
        }

        Spacer(Modifier.height(16.dp))

        // ================= BUTTON =================
        Button(
            onClick = { showDialog = true },
            enabled = uploadState !is PhotoUploadState.Loading,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(44.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.PrimaryGreen
            )
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                if (finalPhotoUrl != null) "Cambia foto" else "Aggiungi foto"
            )
        }
    }

    // ================= DIALOG =================
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Scegli foto") },
            text = { Text("Da dove vuoi prenderla?") },
            confirmButton = {
                Button(onClick = {
                    galleryLauncher.launch("image/*")
                    showDialog = false
                }) {
                    Text("Galleria")
                }
            },
            dismissButton = {
                Button(onClick = {
                    val granted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (granted) cameraLauncher.launch(null)
                    else permissionLauncher.launch(Manifest.permission.CAMERA)

                    showDialog = false
                }) {
                    Text("Camera")
                }
            }
        )
    }
}
