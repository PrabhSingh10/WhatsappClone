package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappclone.model.ChatListModel
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository
import com.example.whatsappclone.repository.RoomRepository
import com.example.whatsappclone.util.Constants.Companion.ONLINE_STATUS
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(
    firebaseAuthRepository: FirebaseAuthRepository,
    private val firebaseStoreRepository: FirebaseStoreRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val userInfo = firebaseAuthRepository.auth.currentUser?.uid.toString()
    val friends = MutableLiveData<List<ChatListModel>>()

    init {
        fetchingChat()  //Fetches the data of the recent chats and new friends from firebase to store it in room
        fetchingFriends()   //Fetches the same data from the room so that the view can observe it and show it in the fragment
    }

    private fun fetchingChat() = viewModelScope.launch {
        firebaseStoreRepository.fetchingChat(userInfo).collect{
            roomRepository.upsertChat(it)
        }
    }

    private fun fetchingFriends() = viewModelScope.launch {
        roomRepository.fetchFriends().collect{
            friends.postValue(it)
        }
    }

    fun updateOnlineStatus(status : String) = viewModelScope.launch {
        val onlineStatus = mutableMapOf<String, String>().also {
            it[ONLINE_STATUS] = status
        }
        firebaseStoreRepository.updateProfile(userInfo, onlineStatus)
    }

    fun clearRoom() = viewModelScope.launch {
        roomRepository.clearRoom()
    }
}