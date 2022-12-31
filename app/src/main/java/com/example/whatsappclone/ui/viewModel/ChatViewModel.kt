package com.example.whatsappclone.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappclone.model.ChatListModel
import com.example.whatsappclone.repository.FirebaseAuthRepository
import com.example.whatsappclone.repository.FirebaseStoreRepository
import kotlinx.coroutines.launch

class ChatViewModel(
    firebaseAuthRepository: FirebaseAuthRepository,
    private val firebaseStoreRepository: FirebaseStoreRepository
) : ViewModel() {

    private val userInfo = firebaseAuthRepository.auth.currentUser?.uid.toString()
    val friendList = MutableLiveData<MutableList<ChatListModel>>()

    fun fetchingChat() = viewModelScope.launch {
        firebaseStoreRepository.fetchingChat(userInfo).collect{
            friendList.postValue(it)
        }

    }
}