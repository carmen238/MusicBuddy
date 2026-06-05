package com.example.musicbuddy.network

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import com.example.musicbuddy.data.models.PhotoUploadResponse
import retrofit2.Response

/**
 * PhotoApiService - API interface for photo upload
 */
interface PhotoApiService {

    /**
     * Upload user profile photo
     ** @param photo Image file as multipart body
     * @return PhotoUploadResponse with photo URL
     */


    @Multipart
    @POST("api/users/upload-photo")
    suspend fun uploadProfilePhoto(
        @Part photo: MultipartBody.Part
    ): Response<PhotoUploadResponse>
}