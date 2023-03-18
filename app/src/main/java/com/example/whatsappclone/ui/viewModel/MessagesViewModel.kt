package com.example.whatsappclone.ui.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappclone.model.MessageModel
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository
import com.example.whatsappclone.repository.RoomRepository
import com.example.whatsappclone.util.Constants
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MessagesViewModel(
    firebaseAuthRepository: FirebaseAuthRepository,
    private val firebaseStoreRepository: FirebaseStoreRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val userInfo = firebaseAuthRepository.auth.currentUser?.uid.toString()
    val onlineStatus = MutableLiveData<Boolean>()
    val typingStatus = MutableLiveData<Boolean>()
    val messages = MutableLiveData<List<MessageModel>>()
    var chatroomId: String? = null

    fun checkOnlineStatus(friendInfo: String) = viewModelScope.launch {
        firebaseStoreRepository.onlineStatus(friendInfo).collect {
            onlineStatus.postValue(it)
        }
    }

    fun updateOnlineStatus(status: String) = viewModelScope.launch {
        val onlineStatus = mutableMapOf<String, String>().also {
            it[Constants.ONLINE_STATUS] = status
        }
        firebaseStoreRepository.updateProfile(userInfo, onlineStatus)
    }

    fun checkTypingStatus(friendInfo: String) = viewModelScope.launch {
        firebaseStoreRepository.checkTypingStatus(userInfo, friendInfo).collect {
            typingStatus.postValue(it)
        }
    }

    fun updateTypingStatus(friendInfo: String, status: String) = viewModelScope.launch {
        val typingStatus = mutableMapOf<String, String>().also {
            it[Constants.TYPING_STATUS] = status
        }
        firebaseStoreRepository.updateTypingStatus(userInfo, friendInfo, typingStatus)
    }

    fun fetchMessage() = viewModelScope.launch {
        firebaseStoreRepository.fetchMessages(chatroomId!!).collect {
            roomRepository.upsertMessage(it)
            fetchMessageRoom()
        }
    }

    private fun fetchMessageRoom() = viewModelScope.launch {
        roomRepository.fetchMessage(chatroomId!!).collect {
            messages.postValue(it)
        }
    }

    fun sendMessage(
        friendInfo: String, message: String, timeStamp: String
    ) = viewModelScope.launch {
        firebaseStoreRepository.sendMessage(
            userInfo, friendInfo, message, timeStamp, chatroomId!!
        )
    }

    fun messageStatus() = viewModelScope.launch {
        firebaseStoreRepository.messageStatus(chatroomId!!)
    }
}