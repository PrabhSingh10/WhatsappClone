package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappclone.model.MessageModel
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository
import com.example.whatsappclone.util.Constants
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MessagesViewModel(
    firebaseAuthRepository: FirebaseAuthRepository,
    private val firebaseStoreRepository: FirebaseStoreRepository
) : ViewModel(){

    private val userInfo = firebaseAuthRepository.auth.currentUser?.uid.toString()
    val chats = MutableLiveData<MutableList<MessageModel>>()
    val onlineStatus = MutableLiveData<Boolean>()
    var chatroomId : String? = null

    fun checkOnlineStatus(friendInfo: String) = viewModelScope.launch {
        firebaseStoreRepository.onlineStatus(friendInfo).collect{
            onlineStatus.postValue(it)
        }
    }

    fun updateOnlineStatus(status : String) = viewModelScope.launch {
        val onlineStatus = mutableMapOf<String, String>().also {
            it[Constants.ONLINE_STATUS] = status
        }
        firebaseStoreRepository.updateProfile(userInfo, onlineStatus)
    }

    fun sendMessage(
        friendInfo: String, message : String, timeStamp: String
    ) = viewModelScope.launch{
        firebaseStoreRepository.sendMessage(
            userInfo, friendInfo, message, timeStamp, chatroomId!!
        )
    }

    fun fetchMessage() = viewModelScope.launch{
        firebaseStoreRepository.fetchMessages(chatroomId!!).collect{
            chats.postValue(it)
        }
    }

    fun messageStatus() = viewModelScope.launch {
        firebaseStoreRepository.messageStatus(chatroomId!!)
    }
}