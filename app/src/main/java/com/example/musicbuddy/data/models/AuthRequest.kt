package com.example.musicbuddy.data.models

/**
 * RegisterRequest - Data class for user registration
 */
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
    val phone: String,
    val bio: String,
    val instrument: String,
    val experienceLevel: String,
    val genre: String,
    val isInBand: Boolean = false,
    val photoUrl: String
)

/**
 * LoginRequest - Data class for user login
 */
data class LoginRequest(
    val email: String,
    val password: String
)

data class UpdateFieldRequest(
    val idUser: String,
    val keyField: String,
    val valueField: String  // ✅ Cambia da Any a String
)

data class DeleteUserRequest(
    val userId: Int
)

data class UpdateLatLongRequest(
    val userId: Int,
    val latValue: Double,
    val longValue: Double
)

data class GetNearbyMusiciansRequest(
    val userId: Int,
    val userLat: Double,
    val userLong: Double,
    val range: Double
)

data class FriendRequestField(   //unico sia per sendFriendRequest, acceptFriendRequest e deleteFriendRequest
    val senderId: Int,
    val receiverId: Int
)

data class GetAllFriendsRequest(
    val userId: Int
)