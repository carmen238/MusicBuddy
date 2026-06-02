package com.example.musicbuddy.data.models

/**
 * PhotoUploadResponse - Response from photo upload endpoint
 */
data class PhotoUploadResponse(
    val success: Boolean,
    val photoUrl: String? = null,
    val message: String? = null,
    val error: String? = null
)