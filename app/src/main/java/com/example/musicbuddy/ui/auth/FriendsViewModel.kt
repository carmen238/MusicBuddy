package com.example.musicbuddy.ui.auth

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicbuddy.data.models.DeleteUserRequest
import com.example.musicbuddy.data.models.*
import com.example.musicbuddy.network.RetrofitClient
import com.example.musicbuddy.ui.auth.AuthState
import com.example.musicbuddy.ui.viewmodels.LocationState
import com.example.musicbuddy.ui.viewmodels.NearbyMusiciansState
import com.example.musicbuddy.ui.viewmodels.UserLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.http.Body
import kotlin.Int

class FriendsViewModel : ViewModel() {

    private val authApiService = RetrofitClient.getAuthApiService()
    private val friendApiService = RetrofitClient.getFriendApiService()

    private val _allFriends = MutableStateFlow<List<FriendInfo>>(emptyList())
    val allFriends: StateFlow<List<FriendInfo>> = _allFriends

    /**
     * Send friend request to a musician
     */
    fun sendFriendRequest(userId: Int, friendId: Int) {
        viewModelScope.launch {
            try {
                val request = FriendRequestField(userId, friendId)

                val response = friendApiService.sendFriendRequest(request)

                if(!response.success) Log.e("FriendsViewModel", "Error during sending friend request: ${response.message}")

                Log.d("FriendsViewModel", "Friend request sent")

            } catch (e: Exception) {
                Log.e("FriendsViewModel", "Error during sending friend request: ${e.message}")
            }
        }
    }

    fun getAllFriends(userId: Int) {
        viewModelScope.launch {
            try {
                val request = GetAllFriendsRequest(userId)

                val response = friendApiService.getAllFriends(request)

                if(!response.success) Log.e("FriendsViewModel", "Error during fetching nearby musicians: ${response.message}")

                _allFriends.value = response.data

                Log.d("FriendsViewModel", "Found ${response.data.size} friends (accepted and pending)")

            } catch (e: Exception) {
                Log.e("FriendsViewModel", "Error fetching friends: ${e.message}")
            }
        }
    }

    fun acceptFriendRequest(userId: Int, friendId: Int) {
        viewModelScope.launch {
            try {
                //Il receiver in questo caso è l'utente che accetta
                val request = FriendRequestField(friendId, userId)

                val response = friendApiService.acceptFriendRequest(request)

                if(!response.success) Log.e("FriendsViewModel", "Error during accepting friend request: ${response.message}")

                Log.d("FriendsViewModel", "Friend request sent")

                val request2 = GetAllFriendsRequest(userId)

                val response2 = friendApiService.getAllFriends(request2)

                if(!response2.success) Log.e("FriendsViewModel", "Error during fetching nearby musicians: ${response2.message}")

                _allFriends.value = response2.data

            } catch (e: Exception) {
                Log.e("FriendsViewModel", "Error during sending friend request: ${e.message}")
            }
        }
    }

    fun deleteFriendRequest(senderId: Int, receiverId: Int, userIsSender: Boolean) {
        viewModelScope.launch {
            try {
                //Nel caso di cancellazione della richiesta, il senderId è l'utente che cancella la richiesta, altrimenti se una richiesta è rigettata è il contrario (gestito lato client)
                val request = FriendRequestField(senderId, receiverId)

                val response = friendApiService.deleteFriendRequest(request)

                if(!response.success) Log.e("FriendsViewModel", "Error during deleting friend request: ${response.message}")

                Log.d("FriendsViewModel", "Friend request deleted")

                val request2: GetAllFriendsRequest

                if(userIsSender) request2 = GetAllFriendsRequest(senderId)
                else request2 = GetAllFriendsRequest(receiverId)

                val response2 = friendApiService.getAllFriends(request2)

                if(!response2.success) Log.e("FriendsViewModel", "Error during fetching nearby musicians: ${response2.message}")

                _allFriends.value = response2.data

            } catch (e: Exception) {
                Log.e("FriendsViewModel", "Error during deleting friend request: ${e.message}")
            }
        }
    }
}