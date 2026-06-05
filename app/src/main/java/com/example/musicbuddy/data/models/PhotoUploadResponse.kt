package com.example.musicbuddy.data.models

/**
 * PhotoUploadResponse - Response from photo upload endpoint
 */
data class PhotoUploadResponse(
    val photoUrl: String? = null,
    val success: Boolean? = null,
    val message: String? = null,
    val error: String? = null
)