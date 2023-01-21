package com.example.whatsappclone.ui.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappclone.model.MessageModel
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository
import kotlinx.coroutines.launch

class MessagesViewModel(
    firebaseAuthRepository: FirebaseAuthRepository,
    private val firebaseStoreRepository: FirebaseStoreRepository
) : ViewModel(){

    private val userInfo = firebaseAuthRepository.auth.currentUser?.uid.toString()
    val chats = MutableLiveData<MutableList<MessageModel>>()
    var chatroomId : String? = null

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