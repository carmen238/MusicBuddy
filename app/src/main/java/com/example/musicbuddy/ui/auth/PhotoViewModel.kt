package com.example.musicbuddy.ui.auth

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicbuddy.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

/**
 * PhotoViewModel - Manages photo upload and compression
 */
class PhotoViewModel : ViewModel() {

    private val photoApiService = RetrofitClient.getPhotoApiService()

    private val _photoUploadState =
        MutableStateFlow<PhotoUploadState>(PhotoUploadState.Idle)
    val photoUploadState: StateFlow<PhotoUploadState> = _photoUploadState

    private val _photoUrl = MutableStateFlow<String?>(null)
    val photoUrl: StateFlow<String?> = _photoUrl

    /**
     * Upload photo from URI
     */
    fun uploadPhoto(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            try {
                _photoUploadState.value = PhotoUploadState.Loading

                // Convert URI to File
                val imageFile = uriToFile(context, imageUri)
                if (imageFile == null) {
                    _photoUploadState.value =
                        PhotoUploadState.Error("Failed to process image")
                    return@launch
                }

                // Compress image
                val compressedFile = compressImage(imageFile)

                // Create multipart request
                val requestBody = compressedFile
                    .asRequestBody("image/jpeg".toMediaType())

                val photoPart = MultipartBody.Part.createFormData(
                    "photo",
                    compressedFile.name,
                    requestBody
                )

                // 🚀 CALL BACKEND (Cloudinary)
                val response = photoApiService.uploadProfilePhoto(photoPart)

                if (response.isSuccessful) {
                    val body = response.body()

                    val url = body?.photoUrl

                    if (url != null) {
                        _photoUrl.value = url
                        _photoUploadState.value = PhotoUploadState.Success(url)
                    } else {
                        _photoUploadState.value = PhotoUploadState.Error("photoUrl null")
                    }

                } else {
                    _photoUploadState.value =
                        PhotoUploadState.Error("HTTP ${response.code()}")
                }
                // Cleanup
                imageFile.delete()
                compressedFile.delete()

            } catch (e: Exception) {
                Log.e("PhotoViewModel", "Upload error: ${e.message}")
                _photoUploadState.value =
                    PhotoUploadState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Convert URI to File
     */
    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(
                context.cacheDir,
                "temp_image_${System.currentTimeMillis()}.jpg"
            )

            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            tempFile
        } catch (e: Exception) {
            Log.e("PhotoViewModel", "URI error: ${e.message}")
            null
        }
    }

    /**
     * Compress image
     */
    private fun compressImage(imageFile: File): File {
        return try {
            val originalBitmap =
                BitmapFactory.decodeFile(imageFile.absolutePath)

            val maxDimension = 1024

            val scale = if (originalBitmap.width > originalBitmap.height) {
                maxDimension.toFloat() / originalBitmap.width
            } else {
                maxDimension.toFloat() / originalBitmap.height
            }

            val newWidth = (originalBitmap.width * scale).toInt()
            val newHeight = (originalBitmap.height * scale).toInt()

            val scaledBitmap = Bitmap.createScaledBitmap(
                originalBitmap,
                newWidth,
                newHeight,
                true
            )

            val compressedFile = File(
                imageFile.parent,
                "compressed_${System.currentTimeMillis()}.jpg"
            )

            FileOutputStream(compressedFile).use { output ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
            }

            Log.d(
                "PhotoViewModel",
                " Compressed: ${imageFile.length()} → ${compressedFile.length()}"
            )

            compressedFile

        } catch (e: Exception) {
            Log.e("PhotoViewModel", "Compression error: ${e.message}")
            imageFile
        }
    }

    /**
     * Clear state
     */
    fun clearState() {
        _photoUploadState.value = PhotoUploadState.Idle
    }
}

/**
 * Upload states
 */
sealed class PhotoUploadState {
    object Idle : PhotoUploadState()
    object Loading : PhotoUploadState()
    data class Success(val photoUrl: String) : PhotoUploadState()
    data class Error(val message: String) : PhotoUploadState()
}