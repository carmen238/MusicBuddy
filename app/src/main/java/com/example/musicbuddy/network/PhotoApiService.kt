package com.example.musicbuddy.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import com.example.musicbuddy.data.models.PhotoUploadResponse

/**
 * PhotoApiService - API interface for photo upload
 */
interface PhotoApiService {

    /**
     * Upload user profile photo
     *
     * @param userId User ID
     * @param photo Image file as multipart body
     * @return PhotoUploadResponse with photo URL
     */
    @Multipart
    @POST("api/users/profile/photo")
    suspend fun uploadProfilePhoto(
        @Part("userId") userId: RequestBody,
        @Part photo: MultipartBody.Part
    ): PhotoUploadResponse
}